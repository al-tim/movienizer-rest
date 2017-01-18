package com.movienizer.data.model;

public class Page<T> {
	private T data;
	private int size;
	private int fromIndex;
	private int toIndex;

	public Page(T data, int size, int fromIndex, int toIndex) {
		super();
		this.data = data;
		this.size = size;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}

	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	public int getFromIndex() {
		return fromIndex;
	}

	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}

	public int getToIndex() {
		return toIndex;
	}

	public void setToIndex(int toIndex) {
		this.toIndex = toIndex;
	}
}