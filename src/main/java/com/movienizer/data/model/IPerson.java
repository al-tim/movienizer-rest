package com.movienizer.data.model;

public interface IPerson {

	public enum fields{Id, Name, Original_Name, Birth_Date, Birthplace, Height, Biography, Awards, Site};

	public Long getId();

	public String getName();

	public String getOriginal_Name();

	public String getBirth_Date();

	public String getBirthplace();

	public Integer getHeight();

	public String getBiography();

	public String getAwards();

	public String getSite();
}