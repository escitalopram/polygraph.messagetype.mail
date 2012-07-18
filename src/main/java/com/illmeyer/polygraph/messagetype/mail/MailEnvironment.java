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
import java.util.Stack;
import java.util.UUID;

import com.illmeyer.polygraph.messagetype.mail.model.Body;

import freemarker.core.Environment;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class MailEnvironment {

	public static void registerParamStringValue(Map<String,Object> src, String name, Map<String,String> dest) throws TemplateModelException {
		Object o = src.get(name);
		if (o instanceof TemplateScalarModel) {
			String s = ((TemplateScalarModel)o).getAsString();
			if (s!=null && !s.trim().isEmpty())
				dest.put(name, s);
		}
	}
	
	public static Stack<String> getTagStack(Environment env) {
		Object oTagStack = env.getCustomAttribute(MailConstants.ECA_TAGSTACK);
		if (oTagStack != null && oTagStack instanceof Stack) {
			@SuppressWarnings("unchecked")
			Stack<String> result = (Stack<String>)oTagStack; 
			return result;
		}
		Stack<String> result = new Stack<>();
		env.setCustomAttribute(MailConstants.ECA_TAGSTACK, result);
		return result;
	}
	
	public static Stack<Body> getPartStack(Environment env) {
		Object oPartStack = env.getCustomAttribute(MailConstants.ECA_PART_STACK);
		if (oPartStack != null && oPartStack instanceof Stack) {
			@SuppressWarnings("unchecked")
			Stack<Body> result = (Stack<Body>)oPartStack; 
			return result;
		}
		Stack<Body> result = new Stack<>();
		env.setCustomAttribute(MailConstants.ECA_PART_STACK, result);
		return result;
		
	}
	
	public static Map<String,String> getCidMap(Environment env) {
		Object oCidMap = env.getCustomAttribute(MailConstants.ECA_CIDMAP);
		if (oCidMap != null && oCidMap instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String,String> result = (Map<String,String>)oCidMap;
			return result;
		}
		Map<String,String> result = new HashMap<>();
		env.setCustomAttribute(MailConstants.ECA_CIDMAP, result);
		return result;
	}

	public static String createContentId() {
		return UUID.randomUUID().toString().replace("-","").replace("{", "").replace("}", "");
	}

}
