package com.fitzhi.controller;

import lombok.Data;

public @Data class Message {

	private long id;

	private String message;

	public Message(long id, String message) {
		super();
		this.id = id;
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "Message [message=" + message + "]";
	}
	
}
