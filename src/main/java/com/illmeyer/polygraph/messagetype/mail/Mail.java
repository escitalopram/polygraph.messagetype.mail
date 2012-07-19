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

import java.util.HashMap;
import java.util.Map;

import lombok.extern.apachecommons.CommonsLog;

import com.illmeyer.polygraph.core.CoreConstants;
import com.illmeyer.polygraph.core.data.Message;
import com.illmeyer.polygraph.core.data.MessagePart;
import com.illmeyer.polygraph.core.spi.MessageType;
import com.illmeyer.polygraph.messagetype.mail.directives.BccDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.CcDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.FromDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.HeaderDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.MailDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.MimeDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.RcptDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.ResourceDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.SenderDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.SubjectDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.ToDirective;
import com.illmeyer.polygraph.messagetype.mail.model.Body;
import com.illmeyer.polygraph.messagetype.mail.model.Document;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;

import freemarker.core.Environment;

@CommonsLog
public class Mail implements MessageType {

	@Override
	public Map<String, Object> createContext() {
		Map<String, Object> result = new HashMap<String,Object>();
		result.put(MailConstants.TAG_MAIL, new MailDirective());
		result.put(MailConstants.TAG_MIME, new MimeDirective());
		result.put(MailConstants.TAG_RESOURCE, new ResourceDirective());
		result.put(MailConstants.TAG_BCC, new BccDirective());
		result.put(MailConstants.TAG_CC, new CcDirective());
		result.put(MailConstants.TAG_RCPT, new RcptDirective());
		result.put(MailConstants.TAG_TO, new ToDirective());
		result.put(MailConstants.TAG_FROM, new FromDirective());
		result.put(MailConstants.TAG_SENDER,new SenderDirective());
		result.put(MailConstants.TAG_SUBJECT, new SubjectDirective());
		result.put(MailConstants.TAG_HEADER, new HeaderDirective());
		return result;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void initialize() {
		System.out.println("Initialized Message type Mail");
	}

	@Override
	public Message createMessage(String messageText, Environment environment) {
		MailDescription md = (MailDescription) environment.getCustomAttribute(MailConstants.ECA_MAILDESC);
		Message msg = new Message(getClass().getName());
		msg.getProperties().put(MailConstants.MP_MAILDESC, md);
		@SuppressWarnings("unchecked")
		Map<String, MessagePart> allParts = (Map<String, MessagePart>) environment.getCustomAttribute(CoreConstants.ECA_PARTS);
		if (allParts==null) {
			// TODO throw some exception
			log.error("will not create empty message");
			return null;
		}
		registerParts(msg,md.getRootElement(),allParts);
		return msg;
	}

	private void registerParts(Message msg, Body body, Map<String, MessagePart> allParts) {
		if (body instanceof MimeBody) { 
			for(Body b : ((MimeBody) body).getSubElements()) registerParts(msg, b, allParts);
		} else {
			Document d = (Document) body;
			msg.getParts().put(d.getPartname(), allParts.get(d.getPartname()));
		}
	}
}
