package com.movienizer.data.model;

public class Person implements IPerson {

	private Long id;
	private String name;
	private String original_Name;
	private String birth_Date;
	private String birthplace;
	private Integer height;
	private String biography;
	private String awards;
	private String site;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getOriginal_Name() {
		return original_Name;
	}

	public void setOriginal_Name(String original_Name) {
		this.original_Name = original_Name;
	}

	@Override
	public String getBirth_Date() {
		return birth_Date;
	}

	public void setBirth_Date(String birth_Date) {
		this.birth_Date = birth_Date;
	}

	@Override
	public String getBirthplace() {
		return birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	@Override
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	@Override
	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	@Override
	public String getAwards() {
		return awards;
	}

	public void setAwards(String awards) {
		this.awards = awards;
	}

	@Override
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
}