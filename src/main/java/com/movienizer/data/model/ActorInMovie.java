package com.movienizer.data.model;

import java.util.SortedSet;

public class ActorInMovie implements IActorInMovie {
	private String character;
	private Long sort_Order;
	private IPerson encapsulatedActor;

	public ActorInMovie(IPerson encapsulatedActor, String character, Long sort_Order) {
		this.encapsulatedActor = encapsulatedActor;
		this.character = character;
		this.sort_Order = sort_Order;
	}

	@Override
	public String getCharacter() {
		return character;
	}

	@Override
	public Long getId() {
		return encapsulatedActor.getId();
	}

	@Override
	public String getName() {
		return encapsulatedActor.getName();
	}

	@Override
	public String getOriginal_Name() {
		return encapsulatedActor.getOriginal_Name();
	}

	@Override
	public String getBirth_Date() {
		return encapsulatedActor.getBirth_Date();
	}

	@Override
	public String getBirthplace() {
		return encapsulatedActor.getBirthplace();
	}

	@Override
	public Integer getHeight() {
		return encapsulatedActor.getHeight();
	}

	@Override
	public String getBiography() {
		return encapsulatedActor.getBiography();
	}

	@Override
	public String getAwards() {
		return encapsulatedActor.getAwards();
	}

	@Override
	public String getSite() {
		return encapsulatedActor.getSite();
	}

	@Override
	public Long getSort_Order() {
		return sort_Order;
	}

	@Override
	public SortedSet<IImage> getPhotos() {
		return encapsulatedActor.getPhotos();
	}
}