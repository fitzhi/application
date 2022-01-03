package com.fitzhi.bean.impl;

public enum RepositoryState {
	
	REPOSITORY_NOT_FOUND (0), 
	REPOSITORY_OUT_OF_DATE (1),
	REPOSITORY_READY (2);

	private int value;

	/**
	 * Constructor of the <code>enum</code> with one unique value parameter
	 * @param value value of the <code>enum</code>
	 */
	RepositoryState(int value) {
		this.value = value;
	}
	
	/**
	 * @return the value of this <code>enum</code>.<br/>
	 */
	public int getValue() {
		return value;
	}

}
