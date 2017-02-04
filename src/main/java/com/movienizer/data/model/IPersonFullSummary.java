package com.movienizer.data.model;

import java.util.SortedSet;

public interface IPersonFullSummary extends IPerson {

	public SortedSet<Movie4PersonFullSummary> getDirectorFor();

	public SortedSet<Movie4PersonFullSummary> getWriterFor();

	public SortedSet<Movie4PersonFullSummary> getActorFor();
}