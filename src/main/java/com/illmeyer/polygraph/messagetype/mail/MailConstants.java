/*
This file is part of the Polygraph bulk messaging framework
Copyright (C) 2012 Wolfgang Illmeyer

The Polygraph framework is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.illmeyer.polygraph.messagetype.mail;

public final class MailConstants {

	/*
	 * keys for environment custom attributes used by this messagetype 
	 */
	public static final String ECA_MAILDESC="com.illmeyer.polygraph.messagetype.mail.Maildesc";
	public static final String ECA_INMAIL="com.illmeyer.polygraph.messagetype.mail.InMail";
	public static final String ECA_PART_STACK="com.illmeyer.polygraph.messagetype.mail.PartStack";
	public static final String ECA_MAIL_TYPE="com.illmeyer.polygraph.messagetype.mail.MailType";
	public static final String ECA_TAGSTACK = "com.illmeyer.polygraph.messagetype.mail.TagStack";
	
	/*
	 * tag names of directives used by this messagetype 
	 */
	public static final String TAG_MAIL="mail";
	public static final String TAG_FROM="from";
	public static final String TAG_SENDER="sender";
	public static final String TAG_TO="to";
	public static final String TAG_CC="cc";
	public static final String TAG_BCC="bcc";
	public static final String TAG_RCPT="rcpt";
	public static final String TAG_ATTACHMENT="attachment";
	public static final String TAG_EMBED="embed";
	public static final String TAG_RESOURCE="resource";
	
	/*
	 * keys for message properties used by this messagetype
	 */
	public static final String MP_MAILDESC = "MailDescription";

	/*
	 * different message specification types used by the mail directive
	 */
	public static final String MTYPE_MIME="mime";
	public static final String MTYPE_SIMPLE="simple";
	
	private MailConstants() {
	}
}
