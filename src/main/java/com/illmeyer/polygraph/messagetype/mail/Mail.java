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

import com.illmeyer.polygraph.core.data.Message;
import com.illmeyer.polygraph.core.data.MessagePart;
import com.illmeyer.polygraph.core.spi.MessageType;
import com.illmeyer.polygraph.messagetype.mail.directives.BccDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.CcDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.RcptDirective;
import com.illmeyer.polygraph.messagetype.mail.directives.ToDirective;

import freemarker.core.Environment;

public class Mail implements MessageType {

	@Override
	public Map<String, Object> createContext() {
		Map<String, Object> result = new HashMap<String,Object>();
		result.put("bcc", new BccDirective());
		result.put("cc", new CcDirective());
		result.put("rcpt", new RcptDirective());
		result.put("to", new ToDirective());
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
		Message m = new Message(this.getClass().getName());
		MessagePart mp = new MessagePart();
		mp.setStringMessage(messageText);
		// TODO: Check charset availability and name equality Java vs Mail
		mp.getProperties().put("Content-Type", "text/plain;charset="+mp.getEncoding());
		m.getParts().put("main", mp);
		return m;
	}
}
