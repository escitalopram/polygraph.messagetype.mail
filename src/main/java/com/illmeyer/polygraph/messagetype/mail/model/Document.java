package com.illmeyer.polygraph.messagetype.mail.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Document implements Body {
	/**
	 * which message part to use for this document
	 */
	private String partname;
	/**
	 * the mime type of this document
	 */
	private String mimeType;
	/**
	 * the content id of this document
	 */
	private String contentId;
	/**
	 * disposition type of this document (optional)
	 */
	private String disposition;
	/**
	 * list of added/changed mail headers for this document
	 */
	private final List<HeaderEdit> headers = new ArrayList<>();
}
