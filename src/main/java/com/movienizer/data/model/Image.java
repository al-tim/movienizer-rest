package com.movienizer.data.model;

public class Image implements IImage {

	private String path;
	private Long sort_Order;

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public Long getSortOrder() {
		return sort_Order;
	}

	public void setSortOrder(Long sort_Order) {
		this.sort_Order = sort_Order;
	}

	@Override
	public int compareTo(IImage o) {
		int result = this.getSortOrder().compareTo(o.getSortOrder());
		return result == 0 ? this.getPath().compareTo(o.getPath()) : result;
	}
}