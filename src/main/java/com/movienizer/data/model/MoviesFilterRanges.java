package com.movienizer.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class MoviesFilterRanges {
	private int minYear = Integer.MAX_VALUE;
	private int maxYear = 0;
	private int minDuration = Integer.MAX_VALUE;
	private int maxDuration = 0;
	private float minKinopoiskRating = Float.MAX_VALUE;
	private float maxKinopoiskRating = 0;
	private float minIMDBRating = Float.MAX_VALUE;
	private float maxIMDBRating = 0;
    private List<String> genres = new ArrayList<String>();
    private List<String> countries = new ArrayList<String>();
    private List<String> studios = new ArrayList<String>();

	public MoviesFilterRanges(SortedSet<String> genres, SortedSet<String> countries, SortedSet<String> studios) {
		for (String value:genres) {
			this.genres.add(value);
		}
		for (String value:countries) {
			this.countries.add(value);
		}
		for (String value:studios) {
			this.studios.add(value);
		}
	}

	public int getMinYear() {
		return minYear;
	}

	public void setMinYear(int minYear) {
		this.minYear = minYear;
	}

	public int getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(int maxYear) {
		this.maxYear = maxYear;
	}

	public int getMinDuration() {
		return minDuration;
	}

	public void setMinDuration(int minDuration) {
		this.minDuration = minDuration;
	}

	public int getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(int maxDuration) {
		this.maxDuration = maxDuration;
	}

	public float getMinKinopoiskRating() {
		return minKinopoiskRating;
	}

	public void setMinKinopoiskRating(float minKinopoiskRating) {
		this.minKinopoiskRating = minKinopoiskRating;
	}

	public float getMaxKinopoiskRating() {
		return maxKinopoiskRating;
	}

	public void setMaxKinopoiskRating(float maxKinopoiskRating) {
		this.maxKinopoiskRating = maxKinopoiskRating;
	}

	public float getMinIMDBRating() {
		return minIMDBRating;
	}

	public void setMinIMDBRating(float minIMDBRating) {
		this.minIMDBRating = minIMDBRating;
	}

	public float getMaxIMDBRating() {
		return maxIMDBRating;
	}

	public void setMaxIMDBRating(float maxIMDBRating) {
		this.maxIMDBRating = maxIMDBRating;
	}

	public List<String> getGenres() {
		return genres;
	}

	public List<String> getCountries() {
		return countries;
	}

	public List<String> getStudios() {
		return studios;
	}
}