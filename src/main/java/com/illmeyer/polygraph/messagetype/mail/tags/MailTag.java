/*
This file is part of the Polygraph bulk messaging framework
Copyright (C) 2013 Wolfgang Illmeyer

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

package com.illmeyer.polygraph.messagetype.mail.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.output.NullWriter;

import lombok.Getter;

import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.messagetype.mail.model.Body;
import com.illmeyer.polygraph.messagetype.mail.model.Document;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;
import com.illmeyer.polygraph.template.BodyExistence;
import com.illmeyer.polygraph.template.PolygraphEnvironment;
import com.illmeyer.polygraph.template.PolygraphTag;
import com.illmeyer.polygraph.template.PolygraphTemplateException;
import com.illmeyer.polygraph.template.TagInfo;
import com.illmeyer.polygraph.template.TagParameter;

@TagInfo(name="mail", nestable=false, body=BodyExistence.REQUIRED)
public class MailTag implements PolygraphTag {
	public static enum MailType {
		simple,
		mime
	}
	
	@TagParameter @Getter
	MailType type;
	
	@TagParameter(optional=true) @Getter
	String subtype;
	
	@TagParameter(optional=true) @Getter
	String textname;
	
	@TagParameter(optional=true) @Getter
	String htmlname;
	
	@Getter
	MailDescription description;
	
	@Getter
	Stack<Body> partStack = new Stack<>();
	
	@Getter
	Map<String,String> cidMap = new HashMap<String, String>();
	
	@Override
	public void execute(PolygraphEnvironment env) throws IOException {
		description=new MailDescription();
		if (env.getCustomAttribute(MailConstants.ECA_MAILDESC)!=null)
			throw new PolygraphTemplateException("Mail already defined");
		if (type==MailType.simple) {
			MimeBody mainBody = new MimeBody();
			mainBody.setSubType("related");
			partStack.push(mainBody);
			description.setRootElement(mainBody);
			if (htmlname!=null && textname!=null) {
				MimeBody alternative = new MimeBody();
				alternative.setSubType("alternative");
				partStack.push(alternative);
				mainBody.getSubElements().add(alternative);
			}
			if(textname!=null) {
				Document d = new Document();
				d.setMimeType("text/plain");
				d.setPartname(textname);
				((MimeBody)partStack.peek()).getSubElements().add(d);
			}
			if(htmlname!=null) {
				Document d = new Document();
				d.setMimeType("text/html");
				d.setPartname(htmlname);
				((MimeBody)partStack.peek()).getSubElements().add(d);
			}
			if (htmlname!=null && textname!=null)
				partStack.pop();
			env.executeBody(NullWriter.NULL_WRITER);
			if (mainBody.getSubElements().size()==1) {
				description.setRootElement(mainBody.getSubElements().get(0));
			}
		} else if (type==MailType.mime) {
			MimeBody mainBody = new MimeBody();
			mainBody.setSubType(subtype);
			description.setRootElement(mainBody);
			partStack.push(mainBody);
			try {
				env.executeBody(NullWriter.NULL_WRITER);
			} finally {
				partStack.pop();
			}
		} else
			throw new PolygraphTemplateException(String.format("Unsupported mail type '%s'",type));
		env.setCustomAttribute(MailConstants.ECA_MAILDESC, description);
		env.setCustomAttribute(MailConstants.ECA_CIDMAP, cidMap);
	}

}
