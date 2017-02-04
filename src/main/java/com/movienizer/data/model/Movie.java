package com.movienizer.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Movie extends AbstractMovie {

	private Long id;
	private String title;
	private String original_Title;
	private Integer year;
	private String description;
	private Integer duration;
	private Float IMDB_Rating;
	private String budget;
	private String awards;
	private Float kinopoisk_Rating;
	private String site;
	private List<IPerson> directors = new ArrayList<IPerson>();
	private List<IPerson> writers = new ArrayList<IPerson>();
	private List<IActorInMovie> actors = new ArrayList<IActorInMovie>();
	private SortedSet<String> genres = new TreeSet<String>();
	private SortedSet<String> countries = new TreeSet<String>();
	private SortedSet<String> studios = new TreeSet<String>();
	private SortedSet<IImage> imageFrontCovers = new TreeSet<IImage>();
	private SortedSet<IImage> imageScreenshots = new TreeSet<IImage>();
	private SortedSet<IImage> imagePosters = new TreeSet<IImage>();
	private SortedSet<IImage> imageBackdrops = new TreeSet<IImage>();

	public Movie() {}

	public static Movie getNewInstance(IMovie copyFrom) {
		Movie newInstance = new Movie();
		newInstance.id = copyFrom.getId();
		newInstance.title = copyFrom.getTitle();
		newInstance.original_Title = copyFrom.getOriginal_Title();
		newInstance.year = copyFrom.getYear();
		newInstance.description = copyFrom.getDescription();
		newInstance.duration = copyFrom.getDuration();
		newInstance.IMDB_Rating = copyFrom.getIMDB_Rating();
		newInstance.budget = copyFrom.getBudget();
		newInstance.awards = copyFrom.getAwards();
		newInstance.kinopoisk_Rating = copyFrom.getKinopoisk_Rating();
		newInstance.site = copyFrom.getSite();
		newInstance.directors = copyFrom.getDirectors();
		newInstance.writers = copyFrom.getWriters();
		newInstance.actors = copyFrom.getActors();
		newInstance.genres = copyFrom.getGenres();
		newInstance.countries = copyFrom.getCountries();
		newInstance.studios = copyFrom.getStudios();
		newInstance.imageFrontCovers = copyFrom.getImageFrontCovers();
		newInstance.imageScreenshots = copyFrom.getImageScreenshots();
		newInstance.imagePosters = copyFrom.getImagePosters();
		newInstance.imageBackdrops = copyFrom.getImageBackdrops();
		return newInstance;
	}

	@Override
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String getOriginal_Title() {
		return original_Title;
	}
	public void setOriginal_Title(String original_Title) {
		this.original_Title = original_Title;
	}
	@Override
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	@Override
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	@Override
	public Float getIMDB_Rating() {
		return IMDB_Rating;
	}
	public void setIMDB_Rating(Float iMDB_Rating) {
		this.IMDB_Rating = iMDB_Rating;
	}
	@Override
	public String getBudget() {
		return budget;
	}
	public void setBudget(String budget) {
		this.budget = budget;
	}
	@Override
	public String getAwards() {
		return awards;
	}
	public void setAwards(String awards) {
		this.awards = awards;
	}
	@Override
	public Float getKinopoisk_Rating() {
		return kinopoisk_Rating;
	}
	public void setKinopoisk_Rating(Float kinopoisk_Rating) {
		this.kinopoisk_Rating = kinopoisk_Rating;
	}
	@Override
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	@Override
	public List<IPerson> getDirectors() {
		return directors;
	}
	@Override
	public List<IPerson> getWriters() {
		return writers;
	}
	@Override
	public List<IActorInMovie> getActors() {
		return actors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public SortedSet<String> getGenres() {
		return genres;
	}

	@Override
	public SortedSet<String> getCountries() {
		return countries;
	}

	@Override
	public SortedSet<String> getStudios() {
		return studios;
	}

	@Override
	public SortedSet<IImage> getImageFrontCovers() {
		return imageFrontCovers;
	}

	@Override
	public SortedSet<IImage> getImageScreenshots() {
		return imageScreenshots;
	}

	@Override
	public SortedSet<IImage> getImagePosters() {
		return imagePosters;
	}

	@Override
	public SortedSet<IImage> getImageBackdrops() {
		return imageBackdrops;
	}
}