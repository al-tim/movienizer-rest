package com.movienizer.data.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ScoredMovie extends AbstractMovie {

	private Map<String, Float> scoreMap = new HashMap<String, Float>();
	private Float compoundScore = 0f;
	private IMovie encapsulatedMovie;

	public ScoredMovie(IMovie encapsulatedMovie) {
		this.encapsulatedMovie = encapsulatedMovie;
	}

	public Float getCompoundScore() {
		return compoundScore;
	}

	public void setCompoundScore(Float compoundScore) {
		this.compoundScore = compoundScore;
	}

	public float calculateScore(String[] scoreFieldNames) {
		float score = 0;
		for (String fieldName:scoreFieldNames) {
			Float fieldScore = scoreMap.get(fieldName);
			if (fieldScore!=null) score = score + fieldScore;
		}
		compoundScore = score;
		return score;
	}

	@JsonIgnore public Map<String, Float> getScoreMap() {
		return scoreMap;
	}

	@JsonIgnore public IMovie getEncapsulatedMovie() {
		return encapsulatedMovie;
	}

	@Override
	public Long getId() {
		return encapsulatedMovie.getId();
	}

	@Override
	public String getTitle() {
		return encapsulatedMovie.getTitle();
	}

	@Override
	public String getOriginal_Title() {
		return encapsulatedMovie.getOriginal_Title();
	}

	@Override
	public Integer getYear() {
		return encapsulatedMovie.getYear();
	}

	@Override
	public String getDescription() {
		return encapsulatedMovie.getDescription();
	}

	@Override
	public Integer getDuration() {
		return encapsulatedMovie.getDuration();
	}

	@Override
	public Float getIMDB_Rating() {
		return encapsulatedMovie.getIMDB_Rating();
	}

	@Override
	public String getBudget() {
		return encapsulatedMovie.getBudget();
	}

	@Override
	public String getAwards() {
		return encapsulatedMovie.getAwards();
	}

	@Override
	public Float getKinopoisk_Rating() {
		return encapsulatedMovie.getKinopoisk_Rating();
	}

	@Override
	public String getSite() {
		return encapsulatedMovie.getSite();
	}

	@Override
	public List<IPerson> getDirectors() {
		return encapsulatedMovie.getDirectors();
	}

	@Override
	public List<IPerson> getWriters() {
		return encapsulatedMovie.getWriters();
	}

	@Override
	public List<IActorInMovie> getActors() {
		return encapsulatedMovie.getActors();
	}

	@Override
	public SortedSet<String> getGenres() {
		return encapsulatedMovie.getGenres();
	}

	@Override
	public SortedSet<String> getCountries() {
		return encapsulatedMovie.getCountries();
	}

	@Override
	public SortedSet<String> getStudios() {
		return encapsulatedMovie.getStudios();
	}

	@Override
	public SortedSet<IImage> getImageFrontCovers() {
		return encapsulatedMovie.getImageFrontCovers();
	}

	@Override
	public SortedSet<IImage> getImageScreenshots() {
		return encapsulatedMovie.getImageScreenshots();
	}

	@Override
	public SortedSet<IImage> getImagePosters() {
		return encapsulatedMovie.getImagePosters();
	}

	@Override
	public SortedSet<IImage> getImageBackdrops() {
		return encapsulatedMovie.getImageBackdrops();
	}
}