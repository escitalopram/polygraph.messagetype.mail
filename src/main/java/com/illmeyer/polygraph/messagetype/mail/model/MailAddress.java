package com.illmeyer.polygraph.messagetype.mail.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class MailAddress implements Serializable {
	
	private static final long serialVersionUID = -4820759907220848693L;
	
	private final String name;
	private final String email;
}
