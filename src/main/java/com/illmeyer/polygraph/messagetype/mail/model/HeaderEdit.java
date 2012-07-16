package com.illmeyer.polygraph.messagetype.mail.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class HeaderEdit implements Serializable {

	private static final long serialVersionUID = -3642055321229952238L;

	public static enum Operation {
		add,replace,remove
	}
	private final String name;
	private final String content;
	private final Operation operation;
}
