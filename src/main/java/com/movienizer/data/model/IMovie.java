package com.movienizer.data.model;

import java.util.List;
import java.util.SortedSet;

public interface IMovie {

	public enum fields{Id, Title, Original_Title, Year, Description, Duration, IMDB_Rating, Budget, Awards, Kinopoisk_Rating, Site};

	public Long getId();
	public String getTitle();
	public String getOriginal_Title();
	public Integer getYear();
	public String getDescription();
	public Integer getDuration();
	public Float getIMDB_Rating();
	public String getBudget();
	public String getAwards();
	public Float getKinopoisk_Rating();
	public String getSite();
	public List<IPerson> getDirectors();
	public List<IPerson> getWriters();
	public List<IActorInMovie> getActors();
	public SortedSet<String> getGenres();
	public SortedSet<String> getCountries();
	public SortedSet<String> getStudios();
	public SortedSet<IImage> getImageFrontCovers();
	public SortedSet<IImage> getImageScreenshots();
	public SortedSet<IImage> getImagePosters();
	public SortedSet<IImage> getImageBackdrops();
}