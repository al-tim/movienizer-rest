package com.movienizer.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movienizer.data.dao.MovieJDBCDAO;
import com.movienizer.data.exception.ConfigException;
import com.movienizer.data.model.ActorInMovie;
import com.movienizer.data.model.IActorInMovie;
import com.movienizer.data.model.IMovie;
import com.movienizer.data.model.IMoviePersonRelation;
import com.movienizer.data.model.IPerson;
import com.movienizer.data.model.Movie;
import com.movienizer.data.model.MoviesFilterRanges;
import com.movienizer.data.model.Page;
import com.movienizer.data.model.Page4TokenizedSearch;
import com.movienizer.data.model.ScoredMovie;

@RestController
@RequestMapping(value = "/movies")
public class MoviesController {

    private static final String ALL_TEXT_FIELDS = "all_text_fields";
    private static final String LOOKUPSIZE_DEFAULT = "10";
	@Autowired
    private MovieJDBCDAO movieDAO;
	private List<IMovie> allMovies;
	private List<IPerson> allPersons;
	private List<IMoviePersonRelation> allRelations;
	private Map<Long, Set<Long>> personIdSetByMovieId = new HashMap<Long, Set<Long>>();
    private Map<Long, IMovie> movieById = new HashMap<Long, IMovie>();
    private Map<Long, IPerson> personById = new HashMap<Long, IPerson>();
    private SortedSet<String> genres = new TreeSet<String>();
    private SortedSet<String> countries = new TreeSet<String>();
    private SortedSet<String> studios = new TreeSet<String>();

	@PostConstruct
	public void initData() throws DataAccessException, IOException, ConfigException {
		long start = System.currentTimeMillis();
    	private_getMovies();
		movieDAO.populateMovieCharacteristics(movieById);
		for (IMovie movie:private_getMovies()) {
			Collections.sort(movie.getActors(), new Comparator<IActorInMovie>() {
				@Override
				public int compare(IActorInMovie o1, IActorInMovie o2) {
					return o1.getSort_Order().compareTo(o2.getSort_Order());
				}
			});
			genres.addAll(movie.getGenres());
			countries.addAll(movie.getCountries());
			studios.addAll(movie.getStudios());
		}
		private_preparePersonSearchCache();
		for (IMoviePersonRelation relation:private_getMoviePersonRelations()) {
			if (IMoviePersonRelation.roles.Directors.name().equals(relation.getRole())) {
				movieById.get(relation.getMovieId()).getDirectors().add(personById.get(relation.getPersonId()));
			} else if (IMoviePersonRelation.roles.Writers.name().equals(relation.getRole())) {
				movieById.get(relation.getMovieId()).getWriters().add(personById.get(relation.getPersonId()));
			} else if (IMoviePersonRelation.roles.Actors.name().equals(relation.getRole())) {
				movieById.get(relation.getMovieId()).getActors().add(
						new ActorInMovie(personById.get(relation.getPersonId()), relation.getCharacterName(), relation.getSort_Order()));
			}
		}
    	private_prepareMovieSearchCache();
		System.out.println("Data initialization took "+(System.currentTimeMillis()-start)/1000f+" seconds.");
	}

	private List<? extends IMoviePersonRelation> private_getMoviePersonRelations() throws DataAccessException, ConfigException {
        if (allRelations == null) {
            synchronized (MoviesController.class) {
                if (allRelations == null) {
                	List<IMoviePersonRelation> newAllRelations = movieDAO.getAllMoviePersonRelationsInCollection();
                	Map<Long, Set<Long>> newPersonIdSetByMovieId = new HashMap<Long, Set<Long>>();
                	for (IMoviePersonRelation relation:newAllRelations) {
                		Set<Long> personIdSet = newPersonIdSetByMovieId.get(relation.getMovieId());
                		if (personIdSet==null) {
                			personIdSet = new HashSet<Long>();
                			newPersonIdSetByMovieId.put(relation.getMovieId(), personIdSet);
                		}
                		personIdSet.add(relation.getPersonId());
                	}
                	personIdSetByMovieId = newPersonIdSetByMovieId;
                	allRelations = newAllRelations;
                }
            }
        }
        return allRelations;
    }

    private List<? extends IPerson> private_getPersons() throws DataAccessException, ConfigException {
        if (allPersons == null) {
            synchronized (MoviesController.class) {
                if (allPersons == null) {
                	allPersons = movieDAO.getAllPersonsInCollection();
                }
            }
        }
        return allPersons;
    }

	private List<? extends IMovie> private_getMovies() throws DataAccessException, ConfigException {
        if (allMovies == null) {
            synchronized (MoviesController.class) {
                if (allMovies == null) {
                	List<IMovie> newAllMovies = movieDAO.getAllMoviesInCollection();
                	movieById.clear();
                    for (IMovie movie:newAllMovies) {
                    	movieById.put(movie.getId(), movie);
                    }
                	allMovies = newAllMovies;
                }
            }
        }
        return allMovies;
    }

	@RequestMapping(value = "/all/persons/in-collection",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    public @ResponseBody String getPersonsInCollection(
    		@RequestParam(value = "fromIndex", required = false, defaultValue = "0") int fromIndex,
    		@RequestParam(value = "toIndex", required = false, defaultValue = "10") int toIndex,
    		@RequestParam(value = "name", required = false, defaultValue = "") String name) throws DataAccessException, ConfigException, IOException, ParseException {
    	List<? extends IPerson> filteredPersons = StringUtils.isEmpty(name) ? private_getPersons(): private_personSearchByName(name, toIndex);
    	int resultingFromIndex = (fromIndex>=filteredPersons.size()) ? filteredPersons.size() - filteredPersons.size() % (toIndex-fromIndex) : fromIndex;
    	int resultingToIndex = (toIndex>filteredPersons.size()) ? filteredPersons.size() : toIndex;
        return new ObjectMapper().writeValueAsString(
        		new Page<List<? extends IPerson>>(filteredPersons.subList(resultingFromIndex, resultingToIndex), filteredPersons.size(), resultingFromIndex, resultingFromIndex+toIndex-fromIndex));
    }

	private IMovie private_movieMatches(IMovie movie, SortedSet<String> testedProperty, String[] matchValues, boolean matchAll) {
		if (matchValues == null || matchValues.length == 0) {
			return movie;
		} else {
	    	int matchCount =0;
	    	for (String value:matchValues) {
	    		if (testedProperty.contains(value)) matchCount++;
	    	}
			if ((matchAll && matchCount == matchValues.length) || (!matchAll && matchCount>0)) {
				if (!matchAll) {// Update score
					if (!(movie instanceof ScoredMovie)) {
						movie = new ScoredMovie(movie);
					}
					((ScoredMovie)movie).setCompoundScore(((ScoredMovie)movie).getCompoundScore() + matchCount/matchValues.length);
				}
				return movie;
			} else {
				return null;
			}
		}
	}

	@RequestMapping(value = "/in-collection",
    				method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    public @ResponseBody String getMoviesInCollection(
    		@RequestParam(value = "fromIndex", required = false, defaultValue = "0") int fromIndex,
    		@RequestParam(value = "toIndex", required = false, defaultValue = "10") int toIndex,
    		@RequestParam(value = "fromYear", required = false, defaultValue = "0") int fromYear,
    		@RequestParam(value = "toYear", required = false, defaultValue = "3000") int toYear,
    		@RequestParam(value = "fromDuration", required = false, defaultValue = "0") int fromDuration,
    		@RequestParam(value = "toDuration", required = false, defaultValue = "500") int toDuration,
    		@RequestParam(value = "fromKinopoisk", required = false, defaultValue = "0") float fromKinopoiskRating,
    		@RequestParam(value = "toKinopoisk", required = false, defaultValue = "10") float toKinopoiskRating,
    		@RequestParam(value = "fromIMDB", required = false, defaultValue = "0") float fromIMDBRating,
    		@RequestParam(value = "toIMDB", required = false, defaultValue = "10") float toIMDBRating,
    		@RequestParam(value = "title", required = false, defaultValue = "") String titleSearch,
    		@RequestParam(value = "originalTitle", required = false, defaultValue = "") String originalTitleSearch,
    		@RequestParam(value = "personIds", required = false, defaultValue = "") String personIds,
    		@RequestParam(value = "personsMatchAll", required = false, defaultValue = "false") boolean personsMatchAll,
    		@RequestParam(value = "genres", required = false, defaultValue = "") String genres,
    		@RequestParam(value = "genresMatchAll", required = false, defaultValue = "false") boolean genresMatchAll,
    		@RequestParam(value = "countries", required = false, defaultValue = "") String countries,
    		@RequestParam(value = "countriesMatchAll", required = false, defaultValue = "false") boolean countriesMatchAll,
    		@RequestParam(value = "studios", required = false, defaultValue = "") String studios,
    		@RequestParam(value = "studiosMatchAll", required = false, defaultValue = "false") boolean studiosMatchAll,
    		@RequestParam(value = "sortFieldName", required = false, defaultValue = "") final String sortFieldName,
    		@RequestParam(value = "sortOrder", required = false, defaultValue = "asc") final String sortOrder,
    		@RequestParam(value = "globalSearch", required = false) String globalSearchString) throws DataAccessException, ConfigException, IOException, ParseException, InterruptedException {
    	private_getMovies();
    	//TimeUnit.SECONDS.sleep(2);
    	List<IMovie> filteredMovies = new ArrayList<IMovie>();
    	List<Long> personIdList = new ArrayList<Long>();
    	if (!StringUtils.isEmpty(personIds.trim())) {
	    	for (String personId:personIds.split(",")) {
	    		personIdList.add(Long.parseLong(personId));
	    	};
    	}
    	String[] genresArray = (StringUtils.isEmpty(genres)) ? new String[0] : genres.trim().split(",");
    	String[] countriesArray = (StringUtils.isEmpty(countries)) ? new String[0] : countries.trim().split(",");
    	String[] studiosArray = (StringUtils.isEmpty(studios)) ? new String[0] : studios.trim().split(",");
    	for (IMovie movie:private_movieSearch(globalSearchString, titleSearch, originalTitleSearch)) {
    		if (movie.getDuration()>=fromDuration && movie.getDuration()<=toDuration
    				&& movie.getYear()>=fromYear && movie.getYear()<=toYear
    				&& (movie.getKinopoisk_Rating()==0 || (movie.getKinopoisk_Rating()>=fromKinopoiskRating && movie.getKinopoisk_Rating()<=toKinopoiskRating))
    				&& (movie.getIMDB_Rating()==0 || (movie.getIMDB_Rating()>=fromIMDBRating && movie.getIMDB_Rating()<=toIMDBRating))) {
    			// Now check person match
    			if (personIdList.size()>0) {
	    			int personMatchCount = 0;
	    			Set<Long> personIdSet4CurrentMovie = personIdSetByMovieId.get(movie.getId());
	    			for (Long personIdToCheck:personIdList) {
	    				if (personIdSet4CurrentMovie.contains(personIdToCheck)) personMatchCount++;
	    			}
	    			if ((personIdList.size()==0 || (personsMatchAll && personMatchCount == personIdList.size()) || (!personsMatchAll && personMatchCount>0))) {
	    				if (!personsMatchAll) {// Update score
	    					if (!(movie instanceof ScoredMovie)) {
	    						movie = new ScoredMovie(movie);
	    					}
	    					if (personIdList.size()>0) {
	    						((ScoredMovie)movie).setCompoundScore(((ScoredMovie)movie).getCompoundScore() + personMatchCount/personIdList.size());
	    					}
	    				}
	    			} else {
	    				continue;// Not matched - skip to next object
	    			}
    			}
    			movie = private_movieMatches(movie, movie.getGenres(), genresArray, genresMatchAll);
    			if (movie==null) continue;// Not matched - skip to next object
    			movie = private_movieMatches(movie, movie.getCountries(), countriesArray, countriesMatchAll);
    			if (movie==null) continue;// Not matched - skip to next object
    			movie = private_movieMatches(movie, movie.getStudios(), studiosArray, studiosMatchAll);
    			if (movie==null) continue;// Not matched - skip to next object

    			filteredMovies.add(movie);
    		}
    	}
    	int resultingFromIndex = (fromIndex>=filteredMovies.size()) ? filteredMovies.size() - filteredMovies.size() % (toIndex-fromIndex) : fromIndex;
    	int resultingToIndex = (toIndex>filteredMovies.size()) ? filteredMovies.size() : toIndex;

    	if (filteredMovies.size()>0) {
    		if (filteredMovies.get(0) instanceof ScoredMovie && StringUtils.isEmpty(sortFieldName)) {
		    	Collections.sort(filteredMovies, new Comparator<IMovie>() {
					@Override
					public int compare(IMovie o1, IMovie o2) {
						return -((ScoredMovie)o1).getCompoundScore().compareTo(((ScoredMovie)o2).getCompoundScore());
					}
				});
    		} else {
		    	Collections.sort(filteredMovies, new Comparator<IMovie>() {
					@Override
					public int compare(IMovie o1, IMovie o2) {
						int result = 0;
						if (StringUtils.isEmpty(sortFieldName)||sortFieldName.equals(IMovie.fields.Title.name())) {
							result = o1.getTitle().compareTo(o2.getTitle());
						} else if (sortFieldName.equals(IMovie.fields.Original_Title.name())) {
							result = o1.getOriginal_Title().compareTo(o2.getOriginal_Title());
						} else if (sortFieldName.equals(IMovie.fields.Year.name())) {
							result = o1.getYear().compareTo(o2.getYear());
						} else if (sortFieldName.equals(IMovie.fields.Kinopoisk_Rating.name())) {
							result = o1.getKinopoisk_Rating().compareTo(o2.getKinopoisk_Rating());
						} else if (sortFieldName.equals(IMovie.fields.IMDB_Rating.name())) {
							result = o1.getIMDB_Rating().compareTo(o2.getIMDB_Rating());
						} else if (sortFieldName.equals(IMovie.fields.Duration.name())) {
							result = o1.getDuration().compareTo(o2.getDuration());
						}
						return sortOrder.equals("desc")?-result:result;
					}
				});
    		}
    	}
    	return new ObjectMapper().writeValueAsString(
        		new Page4TokenizedSearch<List<IMovie>>(
        				filteredMovies.subList(resultingFromIndex, resultingToIndex), filteredMovies.size(), resultingFromIndex, resultingFromIndex+toIndex-fromIndex,
        				private_getLuceneTokens(ALL_TEXT_FIELDS, globalSearchString)
        		));
    }

    @RequestMapping(value = "/in-collection/all/ranges", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    public @ResponseBody String getRanges4MoviesInCollection() throws JsonProcessingException, DataAccessException, ConfigException {
    	private_getMovies();
    	MoviesFilterRanges filterRanges = new MoviesFilterRanges(genres, countries, studios);
    	for (IMovie movie:allMovies) {
    		if (movie.getYear()<filterRanges.getMinYear()) filterRanges.setMinYear(movie.getYear());
    		if (movie.getYear()>filterRanges.getMaxYear()) filterRanges.setMaxYear(movie.getYear());
    		if (movie.getDuration()<filterRanges.getMinDuration()) filterRanges.setMinDuration(movie.getDuration());
    		if (movie.getDuration()>filterRanges.getMaxDuration()) filterRanges.setMaxDuration(movie.getDuration());
    		if (movie.getKinopoisk_Rating() != 0) {
	    		if (movie.getKinopoisk_Rating()<filterRanges.getMinKinopoiskRating()) filterRanges.setMinKinopoiskRating(movie.getKinopoisk_Rating());
	    		if (movie.getKinopoisk_Rating()>filterRanges.getMaxKinopoiskRating()) filterRanges.setMaxKinopoiskRating(movie.getKinopoisk_Rating());
    		}
    		if (movie.getIMDB_Rating() != 0) {
	    		if (movie.getIMDB_Rating()<filterRanges.getMinIMDBRating()) filterRanges.setMinIMDBRating(movie.getIMDB_Rating());
	    		if (movie.getIMDB_Rating()>filterRanges.getMaxIMDBRating()) filterRanges.setMaxIMDBRating(movie.getIMDB_Rating());
    		}
    	}
        return new ObjectMapper().writeValueAsString(filterRanges);
    }

    @RequestMapping(value = "/in-collection/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    public @ResponseBody String getAllMoviesInCollection() throws JsonProcessingException, DataAccessException, ConfigException {
        return new ObjectMapper().writeValueAsString(private_getMovies());
    }

    private static Directory movieSearchDirectory = null;

    private Analyzer getLuceneAnalyzer() throws IOException {// Get both Russian and English stop words while leaving out stemming
    	CharArraySet stopWords = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader(SnowballFilter.class, RussianAnalyzer.DEFAULT_STOPWORD_FILE, StandardCharsets.UTF_8));
    	stopWords.addAll(EnglishAnalyzer.getDefaultStopSet());
    	return new StandardAnalyzer(stopWords);
    }

    private void private_prepareMovieSearchCache() throws IOException, DataAccessException, ConfigException {
    	private_getMovies();
        if (movieSearchDirectory == null) {
            synchronized (MoviesController.class) {
                if (movieSearchDirectory == null) {
                    Directory newSearchDirectory = new RAMDirectory();
                    IndexWriter writer = new IndexWriter(newSearchDirectory, new IndexWriterConfig(getLuceneAnalyzer()));
                    writer.deleteAll();

                    FieldType idFieldType = new FieldType();
                    idFieldType.setStored(true);
                    idFieldType.setIndexOptions(IndexOptions.NONE);
                    idFieldType.setTokenized(false);

                    FieldType titleType = new FieldType();
                    titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
                    titleType.setTokenized(true);
                    titleType.setStored(false);

                    for (IMovie movie:private_getMovies()) {
                        Document document = new Document();
                        document.add(new Field(IMovie.fields.Id.name(), movie.getId().toString(), idFieldType));
                        document.add(new Field(IMovie.fields.Title.name(), movie.getTitle(), titleType));
                        document.add(new Field(IMovie.fields.Original_Title.name(), movie.getOriginal_Title(), titleType));
                        document.add(new Field(IMovie.fields.Description.name(), movie.getDescription(), titleType));
                        document.add(new Field(ALL_TEXT_FIELDS, movie.getTitle(), titleType));
                        document.add(new Field(ALL_TEXT_FIELDS, movie.getOriginal_Title(), titleType));
                        document.add(new Field(ALL_TEXT_FIELDS, movie.getDescription(), titleType));
                        document.add(new Field(ALL_TEXT_FIELDS, movie.getAwards(), titleType));
                        document.add(new Field(ALL_TEXT_FIELDS, movie.getBudget(), titleType));
                        for (String item:movie.getGenres()) {
                            document.add(new Field(ALL_TEXT_FIELDS, item, titleType));
                        }
                        for (String item:movie.getCountries()) {
                            document.add(new Field(ALL_TEXT_FIELDS, item, titleType));
                        }
                        for (String item:movie.getStudios()) {
                            document.add(new Field(ALL_TEXT_FIELDS, item, titleType));
                        }
                        for (IPerson item:movie.getDirectors()) {
                            document.add(new Field(ALL_TEXT_FIELDS, item.getName(), titleType));
                        }
                        for (IPerson item:movie.getWriters()) {
                            document.add(new Field(ALL_TEXT_FIELDS, item.getName(), titleType));
                        }
                        for (IPerson item:movie.getActors()) {
                            document.add(new Field(ALL_TEXT_FIELDS, item.getName(), titleType));
                        }
                        writer.addDocument(document);
                    }
                    writer.close();
                    movieSearchDirectory = newSearchDirectory;
                }
            }
        }
    }

    private List<? extends IMovie> private_movieSearch(String [] searchFields, String [] searchFieldValues) throws DataAccessException, IOException, ConfigException, ParseException {
    	int matchCount = 0;
    	Map<Long, ScoredMovie> filteredMoviesById = new HashMap<Long, ScoredMovie>();
    	for (int i=0; i<searchFields.length; i++) {
	    	if (!StringUtils.isEmpty(searchFieldValues[i])) {
	    		List<ScoredMovie> filteredMovies = private_movieSearch(searchFields[i], searchFieldValues[i], allMovies.size());
	    		for (ScoredMovie movie:filteredMovies) {
	    			ScoredMovie scoredMovie = filteredMoviesById.get(movie.getId());
	    			if (scoredMovie == null) {
	    				filteredMoviesById.put(movie.getId(), movie);
	    			} else {
	    				scoredMovie.getScoreMap().putAll(movie.getScoreMap());
	    			}
	    		}
	    		matchCount++;
	    	}
    	}
    	if (matchCount>0) {
	    	List<ScoredMovie> matchedMovies = new ArrayList<ScoredMovie>();
	    	for (ScoredMovie scoredMovie:filteredMoviesById.values()) {
	    		if (scoredMovie.getScoreMap().size()==matchCount) {
	    			scoredMovie.calculateScore(searchFields);
	    			matchedMovies.add(scoredMovie);
	    		}
	    	}
	    	return matchedMovies;
    	} else {
    		return allMovies;
    	}
    }

    private List<? extends IMovie> private_movieSearch(String globalSearch, String titleSearch, String originalTitleSearch) throws DataAccessException, IOException, ConfigException, ParseException {
		String [] searchFields = {ALL_TEXT_FIELDS, Movie.fields.Title.name(), Movie.fields.Original_Title.name()};
		String [] searchFieldValues = {globalSearch, titleSearch, originalTitleSearch};
		return private_movieSearch(searchFields, searchFieldValues);
    }

    private List<String> private_getLuceneTokens(String fieldName, String searchString) throws IOException {
        List<String> tokens = new ArrayList<String>();
        if (!StringUtils.isEmpty(searchString)) {
	        TokenStream tokenStream = getLuceneAnalyzer().tokenStream(fieldName, searchString);
	        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
	        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
	        tokenStream.reset();
	        while (tokenStream.incrementToken()) {
	            int startOffset = offsetAttribute.startOffset();
	            int endOffset = offsetAttribute.endOffset();
	            tokens.add(charTermAttribute.toString());
	        }
        }
        return tokens;
    }

    private List<ScoredMovie> private_movieSearch(String fieldName, String searchString, Integer lookupSize) throws IOException, DataAccessException, ConfigException, ParseException {
        private_prepareMovieSearchCache();
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(movieSearchDirectory));
        TopFieldDocs hits = searcher.search(new QueryParser(fieldName, getLuceneAnalyzer()).parse(searchString.trim()+"*"), lookupSize, Sort.RELEVANCE, true, false);
        List<ScoredMovie> foundMovies = new ArrayList<ScoredMovie>();
        for (ScoreDoc scoreDocument:hits.scoreDocs) {
        	ScoredMovie movie = new ScoredMovie(movieById.get(new Long(searcher.doc(scoreDocument.doc).get(IMovie.fields.Id.name()))));
            foundMovies.add(movie);
            movie.getScoreMap().put(fieldName, scoreDocument.score);
        }
        return foundMovies;
    }

    @RequestMapping(value = "/search/by-title", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    public @ResponseBody String searchMovieByTitle(@RequestParam("lookup") String searchString, @RequestParam(value="lookupSize", required=false, defaultValue=LOOKUPSIZE_DEFAULT) Integer lookupSize) throws DataAccessException, ConfigException, IOException, ParseException {
        return new ObjectMapper().writeValueAsString(private_movieSearch(IMovie.fields.Title.name(), searchString, lookupSize));
    }

    @RequestMapping(value = "/search/by-description", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    public @ResponseBody String searchMovieByDescription(@RequestParam("lookup") String searchString, @RequestParam(value="lookupSize", required=false, defaultValue=LOOKUPSIZE_DEFAULT) Integer lookupSize) throws DataAccessException, ConfigException, IOException, ParseException {
        return new ObjectMapper().writeValueAsString(private_movieSearch(IMovie.fields.Description.name(), searchString, lookupSize));
    }

    @RequestMapping(value = "/search/by-all-text-fields", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8")
    public @ResponseBody String searchMovieByAllTextFields(@RequestParam("lookup") String searchString, @RequestParam(value="lookupSize", required=false, defaultValue=LOOKUPSIZE_DEFAULT) Integer lookupSize) throws DataAccessException, ConfigException, IOException, ParseException {
        return new ObjectMapper().writeValueAsString(private_movieSearch(ALL_TEXT_FIELDS, searchString, lookupSize));
    }

    private static Directory personSearchDirectory = null;

    private void private_preparePersonSearchCache() throws IOException, DataAccessException, ConfigException {
    	private_getPersons();
        if (personSearchDirectory == null) {
            synchronized (MoviesController.class) {
                if (personSearchDirectory == null) {
                    Directory newSearchDirectory = new RAMDirectory();
                    personById.clear();
                    IndexWriter writer = new IndexWriter(newSearchDirectory, new IndexWriterConfig(getLuceneAnalyzer()));
                    writer.deleteAll();

                    FieldType idFieldType = new FieldType();
                    idFieldType.setStored(true);
                    idFieldType.setIndexOptions(IndexOptions.NONE);
                    idFieldType.setTokenized(false);

                    FieldType nameType = new FieldType();
                    nameType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
                    nameType.setTokenized(true);
                    nameType.setStored(false);

                    for (IPerson person:private_getPersons()) {
                    	personById.put(person.getId(), person);
                        Document document = new Document();
                        document.add(new Field(IPerson.fields.Id.name(), person.getId().toString(), idFieldType));
                        document.add(new Field(IPerson.fields.Name.name(), person.getName(), nameType));
                        writer.addDocument(document);
                    }
                    writer.close();
                    personSearchDirectory = newSearchDirectory;
                }
            }
        }
    }

    private List<? extends IPerson> private_personSearchByName(String name, int lookupSize) throws IOException, DataAccessException, ConfigException, ParseException {
    	private_preparePersonSearchCache();
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(personSearchDirectory));
        TopDocs hits = searcher.search(new QueryParser(IPerson.fields.Name.name(), getLuceneAnalyzer()).parse(name.trim()+"*"), lookupSize, Sort.RELEVANCE, true, false);
        List<IPerson> foundPersons = new ArrayList<IPerson>();
        for (ScoreDoc scoreDocument:hits.scoreDocs) {
        	IPerson person = personById.get(new Long(searcher.doc(scoreDocument.doc).get(IPerson.fields.Id.name())));
            foundPersons.add(person);
        }
        return foundPersons;
    }
}