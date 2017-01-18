package com.movienizer.data.model;

public interface IMoviePersonRelation {

	public enum fields{MovieId, PersonId, Role, CharacterName, Sort_Order};
	public enum roles{Directors, Writers, Actors};

	public Long getMovieId();

	public Long getPersonId();

	public String getRole();

	public String getCharacterName();

	public Long getSort_Order();
}