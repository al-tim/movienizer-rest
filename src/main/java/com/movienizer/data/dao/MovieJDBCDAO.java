package com.movienizer.data.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.movienizer.data.exception.ConfigException;
import com.movienizer.data.model.IMovie;
import com.movienizer.data.model.IMoviePersonRelation;
import com.movienizer.data.model.IPerson;

public interface MovieJDBCDAO {

	public List<IMovie> getAllMoviesInCollection() throws DataAccessException, ConfigException;

	public List<IPerson> getAllPersonsInCollection() throws DataAccessException, ConfigException;

	public List<IMoviePersonRelation> getAllMoviePersonRelationsInCollection() throws DataAccessException, ConfigException;

	public void populateMovieCharacteristics(Map<Long, IMovie> movieById) throws DataAccessException, ConfigException;

	public void populateMovieImages(Map<Long, IMovie> movieById) throws DataAccessException, ConfigException;

	public void populatePersonImages(Map<Long, IPerson> personById) throws DataAccessException, ConfigException;
}