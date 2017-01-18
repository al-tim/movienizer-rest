package com.movienizer.data.model;

import java.util.List;

public class Page4TokenizedSearch<T> extends Page<T> {
	private List<String> globalSearchTokens;

	public Page4TokenizedSearch(T data, int size, int fromIndex, int toIndex, List<String> globalSearchTokens) {
		super(data, size, fromIndex, toIndex);
		this.globalSearchTokens = globalSearchTokens;
	}

	public List<String> getGlobalSearchTokens() {
		return globalSearchTokens;
	}

	public void setGlobalSearchTokens(List<String> globalSearchTokens) {
		this.globalSearchTokens = globalSearchTokens;
	}
}