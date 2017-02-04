package com.movienizer.data.model;

public interface IImage extends Comparable<IImage> {

	public enum fields{Path, Sort_Order, Role};
	public final Long CATEGORY_FRONTCOVER = 1L;
	public final Long CATEGORY_SCREENSHOT = 3L;
	public final Long CATEGORY_POSTER = 4L;
	public final Long CATEGORY_BACKDROP = 7L;

	public String getPath();
	public Long getSortOrder();
}