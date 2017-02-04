package com.movienizer.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.movienizer.data.config.SQLConfig;
import com.movienizer.data.exception.ConfigException;
import com.movienizer.data.model.IImage;
import com.movienizer.data.model.IImageMovieRelation;
import com.movienizer.data.model.IImagePersonRelation;
import com.movienizer.data.model.IMovie;
import com.movienizer.data.model.IMovieCharacteristic;
import com.movienizer.data.model.IMoviePersonRelation;
import com.movienizer.data.model.IPerson;
import com.movienizer.data.model.Image;
import com.movienizer.data.model.Movie;
import com.movienizer.data.model.MoviePersonRelation;
import com.movienizer.data.model.Person;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class MovieJDBCDAOImpl extends NamedParameterJdbcDaoSupport implements MovieJDBCDAO {

	private static class MoviesSQLs {
	    private static final String SQLSET = "Movies";

	    public enum sqls{
	    	getAllMoviesInCollection, getAllPersonsInCollection, getMoviePersonRelations, getMovieCharacteristics, getMovieImages, getPersonImages
	    };

	    public static String getSql(sqls sql) throws ConfigException {
	        String value=SQLConfig.getRetrievalSQL(SQLSET, sql.name());
	        if (value==null) throw new ConfigException("No sql found for "+sql.name());
	        return value;
	    }
	}

	@Override
	public List<IMovie> getAllMoviesInCollection() throws DataAccessException, ConfigException {
		return getNamedParameterJdbcTemplate().query(MoviesSQLs.getSql(MoviesSQLs.sqls.getAllMoviesInCollection),
				new RowMapper<IMovie>() {
					@Override
					public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
						Movie movie = new Movie();
						movie.setId(rs.getLong(IMovie.fields.Id.name()));
						movie.setTitle(rs.getString(IMovie.fields.Title.name()));
						movie.setOriginal_Title(rs.getString(IMovie.fields.Original_Title.name()));
						movie.setYear(rs.getInt(IMovie.fields.Year.name()));
						movie.setDescription(rs.getString(IMovie.fields.Description.name()));
						movie.setDuration(rs.getInt(IMovie.fields.Duration.name()));
						movie.setIMDB_Rating(rs.getFloat(IMovie.fields.IMDB_Rating.name()));
						movie.setBudget(rs.getString(IMovie.fields.Budget.name()));
						movie.setAwards(rs.getString(IMovie.fields.Awards.name()));
						movie.setSite(rs.getString(IMovie.fields.Site.name()));
						movie.setKinopoisk_Rating(rs.getFloat(IMovie.fields.Kinopoisk_Rating.name()));
						return movie;
					}
				});
	}

	@Override
	public List<IPerson> getAllPersonsInCollection() throws DataAccessException, ConfigException {
		final SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS");
		final SimpleDateFormat targetFormat = new SimpleDateFormat("dd-mm-yyyy");
		return getNamedParameterJdbcTemplate().query(MoviesSQLs.getSql(MoviesSQLs.sqls.getAllPersonsInCollection),
				new RowMapper<IPerson>() {
					@Override
					public IPerson mapRow(ResultSet rs, int rowNum) throws SQLException {
						Person person = new Person();
						person.setId(rs.getLong(IPerson.fields.Id.name()));
						person.setName(rs.getString(IPerson.fields.Name.name()));
						person.setOriginal_Name(rs.getString(IPerson.fields.Original_Name.name()));
						String dateString = rs.getString(IPerson.fields.Birth_Date.name());
						try {
							person.setBirth_Date((StringUtils.isEmpty(dateString))?null:targetFormat.format(sourceFormat.parse(dateString)));
						} catch (ParseException e) {
							try {
								Integer.parseInt(dateString);// Just validating it is a single number - presumably year
								person.setBirth_Date(dateString);
							} catch (NumberFormatException e2) {
								throw new SQLException("Cannot parse date: "+dateString, e2);
							}
						}
						person.setBirthplace(rs.getString(IPerson.fields.Birthplace.name()));
						person.setHeight(rs.getInt(IPerson.fields.Height.name()));
						person.setBiography(rs.getString(IPerson.fields.Biography.name()));
						person.setAwards(rs.getString(IPerson.fields.Awards.name()));
						person.setSite(rs.getString(IPerson.fields.Site.name()));
						return person;
					}
				});
	}

	@Override
	public List<IMoviePersonRelation> getAllMoviePersonRelationsInCollection() throws DataAccessException, ConfigException {
		return getNamedParameterJdbcTemplate().query(MoviesSQLs.getSql(MoviesSQLs.sqls.getMoviePersonRelations),
				new RowMapper<IMoviePersonRelation>() {
					@Override
					public IMoviePersonRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
						MoviePersonRelation relation = new MoviePersonRelation();
						relation.setMovieId(rs.getLong(IMoviePersonRelation.fields.MovieId.name()));
						relation.setPersonId(rs.getLong(IMoviePersonRelation.fields.PersonId.name()));
						relation.setRole(rs.getString(IMoviePersonRelation.fields.Role.name()));
						relation.setCharacterName(rs.getString(IMoviePersonRelation.fields.CharacterName.name()));
						relation.setSort_Order(rs.getLong(IMoviePersonRelation.fields.Sort_Order.name()));
						return relation;
					}
				});
	}

	@Override
	public void populateMovieCharacteristics(final Map<Long, IMovie> movieById) throws DataAccessException, ConfigException {
		getNamedParameterJdbcTemplate().query(MoviesSQLs.getSql(MoviesSQLs.sqls.getMovieCharacteristics),
			new RowMapper<Void>() {
				@Override
				public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
					IMovie movie = movieById.get(rs.getLong(IMovieCharacteristic.fields.MovieId.name()));
					Long category = rs.getLong(IMovieCharacteristic.fields.Category.name());
					String characteristicValue = rs.getString(IMovieCharacteristic.fields.CharactersticValue.name());
					if (IMovieCharacteristic.CATEGORY_GENRE.equals(category)) {
						movie.getGenres().add(characteristicValue);
					} else if (IMovieCharacteristic.CATEGORY_COUNTRY.equals(category)) {
						movie.getCountries().add(characteristicValue);
					} else if (IMovieCharacteristic.CATEGORY_STUDIO.equals(category)) {
						movie.getStudios().add(characteristicValue);
					}
					return null;
				}
			});
	}

	@Override
	public void populateMovieImages(final Map<Long, IMovie> movieById) throws DataAccessException, ConfigException {
		getNamedParameterJdbcTemplate().query(MoviesSQLs.getSql(MoviesSQLs.sqls.getMovieImages),
			new RowMapper<Void>() {
				@Override
				public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
					IMovie movie = movieById.get(rs.getLong(IImageMovieRelation.fields.MovieId.name()));
					Image image = new Image();
					image.setPath(rs.getString(IImage.fields.Path.name()).replace('\\', '/'));
					image.setSortOrder(rs.getLong(IImage.fields.Sort_Order.name()));
					Long role = rs.getLong(IImage.fields.Role.name());
					if (IImage.CATEGORY_FRONTCOVER.equals(role)) {
						movie.getImageFrontCovers().add(image);
					} else if (IImage.CATEGORY_SCREENSHOT.equals(role)) {
						movie.getImageScreenshots().add(image);
					} else if (IImage.CATEGORY_POSTER.equals(role)) {
						movie.getImagePosters().add(image);
					} else if (IImage.CATEGORY_BACKDROP.equals(role)) {
						movie.getImageBackdrops().add(image);
					}
					return null;
				}
			});
	}

	@Override
	public void populatePersonImages(final Map<Long, IPerson> personById) throws DataAccessException, ConfigException {
		getNamedParameterJdbcTemplate().query(MoviesSQLs.getSql(MoviesSQLs.sqls.getPersonImages),
				new RowMapper<Void>() {
					@Override
					public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
						IPerson person = personById.get(rs.getLong(IImagePersonRelation.fields.PersonId.name()));
						Image image = new Image();
						image.setPath(rs.getString(IImage.fields.Path.name()).replace('\\', '/'));
						image.setSortOrder(rs.getLong(IImage.fields.Sort_Order.name()));
						person.getPhotos().add(image);
						return null;
					}
				});
	}
}