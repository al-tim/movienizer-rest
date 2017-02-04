package com.movienizer.data.model;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class PersonFullSummary implements IPersonFullSummary {
	private IPerson encapsulatedPerson;
	private SortedSet<Movie4PersonFullSummary> directorFor;
	private SortedSet<Movie4PersonFullSummary> writerFor;
	private SortedSet<Movie4PersonFullSummary> actorFor;

	public PersonFullSummary(IPerson encapsulatedPerson) {
		this.encapsulatedPerson = encapsulatedPerson;
		Comparator<Movie4PersonFullSummary> comparator = new Comparator<Movie4PersonFullSummary>() {
			@Override
			public int compare(Movie4PersonFullSummary o1, Movie4PersonFullSummary o2) {
				int result = o1.getYear().compareTo(o2.getYear());
				if (result==0) o1.getTitle().compareTo(o2.getTitle());
				return (result==0) ? o1.getOriginal_Title().compareTo(o2.getOriginal_Title()) : result;
			}
		};
		directorFor = new TreeSet<Movie4PersonFullSummary>(comparator);
		writerFor = new TreeSet<Movie4PersonFullSummary>(comparator);
		actorFor = new TreeSet<Movie4PersonFullSummary>(comparator);
	}

	@Override
	public Long getId() {
		return encapsulatedPerson.getId();
	}

	@Override
	public String getName() {
		return encapsulatedPerson.getName();
	}

	@Override
	public String getOriginal_Name() {
		return encapsulatedPerson.getOriginal_Name();
	}

	@Override
	public String getBirth_Date() {
		return encapsulatedPerson.getBirth_Date();
	}

	@Override
	public String getBirthplace() {
		return encapsulatedPerson.getBirthplace();
	}

	@Override
	public Integer getHeight() {
		return encapsulatedPerson.getHeight();
	}

	@Override
	public String getBiography() {
		return encapsulatedPerson.getBiography();
	}

	@Override
	public String getAwards() {
		return encapsulatedPerson.getAwards();
	}

	@Override
	public String getSite() {
		return encapsulatedPerson.getSite();
	}

	@Override
	public SortedSet<IImage> getPhotos() {
		return encapsulatedPerson.getPhotos();
	}

	@Override
	public SortedSet<Movie4PersonFullSummary> getDirectorFor() {
		return directorFor;
	}

	@Override
	public SortedSet<Movie4PersonFullSummary> getWriterFor() {
		return writerFor;
	}

	@Override
	public SortedSet<Movie4PersonFullSummary> getActorFor() {
		return actorFor;
	}
}
