package com.illmeyer.polygraph.messagetype.mail.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MimeBody implements Body, Serializable {

	private static final long serialVersionUID = 8045096708044600288L;
	/**
	 * Mime Subtype of this aggregate, e.g. mixed, alternative,...
	 */
	private String subType;
	/**
	 * elements of this aggregate
	 */
	private List<Body> subElements;
	/**
	 * list of additional/changed headers for this mime aggregate
	 */
	private final List<HeaderEdit> headers = new ArrayList<>();
}
