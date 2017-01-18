package com.movienizer.data.model;

public class MoviePersonRelation implements IMoviePersonRelation {

	private Long movieId;
	private Long personId;
	private String Role;
	private String characterName;
	private Long sort_Order;

	@Override
	public Long getMovieId() {
		return movieId;
	}

	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}

	@Override
	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	@Override
	public String getRole() {
		return Role;
	}

	public void setRole(String role) {
		Role = role;
	}

	@Override
	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	@Override
	public Long getSort_Order() {
		return sort_Order;
	}

	public void setSort_Order(Long sort_Order) {
		this.sort_Order = sort_Order;
	}
}