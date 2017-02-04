package com.movienizer.data.model;

public class Movie4PersonFullSummary {
	private IMovie encapsulatedMovie;
	private IImage image;

	public Movie4PersonFullSummary(IMovie encapsulatedMovie) {
		this.encapsulatedMovie = encapsulatedMovie;
		if (this.encapsulatedMovie.getImageFrontCovers()!=null && this.encapsulatedMovie.getImageFrontCovers().size()>0) {
			this.image = this.encapsulatedMovie.getImageFrontCovers().first();
		} else if (this.encapsulatedMovie.getImagePosters()!=null && this.encapsulatedMovie.getImagePosters().size()>0) {
			this.image = this.encapsulatedMovie.getImagePosters().first();
		} else if (this.encapsulatedMovie.getImageBackdrops()!=null && this.encapsulatedMovie.getImageBackdrops().size()>0) {
			this.image = this.encapsulatedMovie.getImageBackdrops().first();
		} else if (this.encapsulatedMovie.getImageScreenshots()!=null && this.encapsulatedMovie.getImageScreenshots().size()>0) {
			this.image = this.encapsulatedMovie.getImageScreenshots().first();
		}
	}

	public Long getId() {
		return this.encapsulatedMovie.getId();
	}

	public String getTitle() {
		return this.encapsulatedMovie.getTitle();
	}

	public String getOriginal_Title() {
		return this.encapsulatedMovie.getOriginal_Title();
	}

	public Integer getYear() {
		return this.encapsulatedMovie.getYear();
	}

	public IImage getImage() {
		return this.image;
	}
}