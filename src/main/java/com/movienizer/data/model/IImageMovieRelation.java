package com.movienizer.data.model;

public interface IImageMovieRelation extends IImage {

	public enum fields{MovieId};

	public Long getMovieId();
}