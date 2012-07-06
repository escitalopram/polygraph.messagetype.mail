package com.illmeyer.polygraph.messagetype.mail;

import java.util.Map;

import com.illmeyer.polygraph.core.data.Message;
import com.illmeyer.polygraph.core.data.MessagePart;
import com.illmeyer.polygraph.core.spi.MessageType;

import freemarker.core.Environment;
import freemarker.template.TemplateHashModel;

public class Mail implements MessageType {

	@Override
	public Map<String, Object> createContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
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
