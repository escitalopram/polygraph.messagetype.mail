package com.illmeyer.polygraph.messagetype.mail.model;

import lombok.Data;

@Data
public class HeaderEdit {
	public static enum Operation {
		add,replace,remove
	}
	private final String name;
	private final String content;
	private final Operation operation;
}
