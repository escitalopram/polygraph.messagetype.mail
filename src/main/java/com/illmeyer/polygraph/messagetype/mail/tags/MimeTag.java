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

import org.apache.commons.io.output.NullWriter;

import com.illmeyer.polygraph.messagetype.mail.model.Body;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;
import com.illmeyer.polygraph.messagetype.mail.tags.MailTag.MailType;
import com.illmeyer.polygraph.template.BodyExistence;
import com.illmeyer.polygraph.template.PolygraphEnvironment;
import com.illmeyer.polygraph.template.PolygraphTag;
import com.illmeyer.polygraph.template.PolygraphTemplateException;
import com.illmeyer.polygraph.template.TagInfo;
import com.illmeyer.polygraph.template.TagParameter;

@TagInfo(name="mime",body=BodyExistence.REQUIRED)
public class MimeTag implements PolygraphTag {

	@TagParameter
	String type;
	
	@Override
	public void execute(PolygraphEnvironment env) throws IOException {
		MailTag m = env.requireAncestorTag(MailTag.class);
		if (m.getType()==MailType.simple)
			throw new PolygraphTemplateException("tag not allowed in mail of type 'simple'");
		Body b = m.getPartStack().peek();
		if (b==null || !(b instanceof MimeBody)) throw new PolygraphTemplateException("mime tag not allowed here");

		MimeBody mb = new MimeBody();
		mb.setSubType(type);
		((MimeBody)m.getPartStack().peek()).getSubElements().add(mb);
		m.getPartStack().push(mb);
		try {
			env.executeBody(NullWriter.NULL_WRITER);
		} finally {
			m.getPartStack().pop();
		}

		if (mb.getSubElements().size()==0)
			throw new PolygraphTemplateException("need at least one mime body (use resource tag)");
	}

}
