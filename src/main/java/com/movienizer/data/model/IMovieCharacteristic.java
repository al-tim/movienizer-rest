package com.movienizer.data.model;

public interface IMovieCharacteristic {

	public enum fields{MovieId, Category, CharactersticId, CharactersticValue}
	public final Long CATEGORY_GENRE = 8L;
	public final Long CATEGORY_COUNTRY = 9L;
	public final Long CATEGORY_STUDIO = 12L;

	public Long getMovieId();
	public Long getCategory();
	public String getCharactersticValue();
	public Long getCharactersticId();
}