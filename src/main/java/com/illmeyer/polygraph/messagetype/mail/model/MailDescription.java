package com.illmeyer.polygraph.messagetype.mail.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MailDescription {
	private MailAddress from;
	/**
	 * list of mail header to recipients 
	 */
	private final List<MailAddress> to = new ArrayList<>();
	/**
	 * list of mail header cc recipients
	 */
	private final List<MailAddress> cc = new ArrayList<>();
	/**
	 * list of mail header bcc recipients (??)
	 */
	private final List<MailAddress> bcc = new ArrayList<>();
	/**
	 * list of envelope recipients. mail header recipients will be used if empty 
	 */
	private final List<String> recipients = new ArrayList<>();
	/**
	 * list of additional mail headers
	 */
	private final List<HeaderEdit> headers = new ArrayList<>();
	/**
	 * content of the mail
	 */
	private Body rootElement;
}
