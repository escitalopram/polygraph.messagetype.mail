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

import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;

import com.illmeyer.polygraph.core.CoreConstants;
import com.illmeyer.polygraph.core.data.Message;
import com.illmeyer.polygraph.core.data.MessagePart;
import com.illmeyer.polygraph.core.data.VersionNumber;
import com.illmeyer.polygraph.core.spi.MessageType;
import com.illmeyer.polygraph.messagetype.mail.model.Body;
import com.illmeyer.polygraph.messagetype.mail.model.Document;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;
import com.illmeyer.polygraph.messagetype.mail.tags.BccTag;
import com.illmeyer.polygraph.messagetype.mail.tags.CcTag;
import com.illmeyer.polygraph.messagetype.mail.tags.FromTag;
import com.illmeyer.polygraph.messagetype.mail.tags.HeaderTag;
import com.illmeyer.polygraph.messagetype.mail.tags.LinkEmbeddedTag;
import com.illmeyer.polygraph.messagetype.mail.tags.MailTag;
import com.illmeyer.polygraph.messagetype.mail.tags.MimeTag;
import com.illmeyer.polygraph.messagetype.mail.tags.RcptTag;
import com.illmeyer.polygraph.messagetype.mail.tags.ResourceTag;
import com.illmeyer.polygraph.messagetype.mail.tags.SenderTag;
import com.illmeyer.polygraph.messagetype.mail.tags.SubjectTag;
import com.illmeyer.polygraph.messagetype.mail.tags.ToTag;
import com.illmeyer.polygraph.template.DefaultTagFactory;
import com.illmeyer.polygraph.template.TagAdapter;

import freemarker.core.Environment;

@CommonsLog
public class Mail implements MessageType {
	
	@Getter
	private final VersionNumber versionNumber = new VersionNumber(0, 1, 0);

	@Override
	public Map<String, Object> createContext() {
		Map<String, Object> result = new HashMap<String,Object>();
		new TagAdapter(new DefaultTagFactory(MailTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(MimeTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(ResourceTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(BccTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(CcTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(RcptTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(ToTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(FromTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(SenderTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(SubjectTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(HeaderTag.class)).register(result);
		new TagAdapter(new DefaultTagFactory(LinkEmbeddedTag.class)).register(result);
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
