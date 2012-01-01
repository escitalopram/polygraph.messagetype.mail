package com.illmeyer.polygraph.messagetype.mail;

import java.util.Map;

import com.illmeyer.polygraph.core.MessageType;

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
	public String postProcessMessage(String message) {
		return "Postprocessed: " + message;
	}

	@Override
	public void initialize() {
		System.out.println("Initialized Message type Mail");
	}

}
