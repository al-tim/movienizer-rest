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
import com.movienizer.data.model.IMovie;
import com.movienizer.data.model.IMovieCharacteristic;
import com.movienizer.data.model.IMoviePersonRelation;
import com.movienizer.data.model.IPerson;
import com.movienizer.data.model.Movie;
import com.movienizer.data.model.MoviePersonRelation;
import com.movienizer.data.model.Person;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class MovieJDBCDAOImpl extends NamedParameterJdbcDaoSupport implements MovieJDBCDAO {

	private static class MoviesSQLs {
	    private static final String SQLSET = "Movies";

	    public enum sqls{
	    	getAllMoviesInCollection, getAllPersonsInCollection, getMoviePersonRelations, getMovieCharacteristics
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
						movie.setId(rs.getLong(Movie.fields.Id.name()));
						movie.setTitle(rs.getString(Movie.fields.Title.name()));
						movie.setOriginal_Title(rs.getString(Movie.fields.Original_Title.name()));
						movie.setYear(rs.getInt(Movie.fields.Year.name()));
						movie.setDescription(rs.getString(Movie.fields.Description.name()));
						movie.setDuration(rs.getInt(Movie.fields.Duration.name()));
						movie.setIMDB_Rating(rs.getFloat(Movie.fields.IMDB_Rating.name()));
						movie.setBudget(rs.getString(Movie.fields.Budget.name()));
						movie.setAwards(rs.getString(Movie.fields.Awards.name()));
						movie.setSite(rs.getString(Movie.fields.Site.name()));
						movie.setKinopoisk_Rating(rs.getFloat(Movie.fields.Kinopoisk_Rating.name()));
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
						person.setId(rs.getLong(Person.fields.Id.name()));
						person.setName(rs.getString(Person.fields.Name.name()));
						person.setOriginal_Name(rs.getString(Person.fields.Original_Name.name()));
						String dateString = rs.getString(Person.fields.Birth_Date.name());
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
						person.setBirthplace(rs.getString(Person.fields.Birthplace.name()));
						person.setHeight(rs.getInt(Person.fields.Height.name()));
						person.setBiography(rs.getString(Person.fields.Biography.name()));
						person.setAwards(rs.getString(Person.fields.Awards.name()));
						person.setSite(rs.getString(Person.fields.Site.name()));
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
						relation.setMovieId(rs.getLong(MoviePersonRelation.fields.MovieId.name()));
						relation.setPersonId(rs.getLong(MoviePersonRelation.fields.PersonId.name()));
						relation.setRole(rs.getString(MoviePersonRelation.fields.Role.name()));
						relation.setCharacterName(rs.getString(MoviePersonRelation.fields.CharacterName.name()));
						relation.setSort_Order(rs.getLong(MoviePersonRelation.fields.Sort_Order.name()));
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
}