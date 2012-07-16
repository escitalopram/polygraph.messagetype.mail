package com.illmeyer.polygraph.messagetype.mail.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MimeBody implements Body {
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
