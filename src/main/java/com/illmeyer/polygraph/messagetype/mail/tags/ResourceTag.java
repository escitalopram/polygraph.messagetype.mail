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

import lombok.Getter;

import com.illmeyer.polygraph.messagetype.mail.MailEnvironment;
import com.illmeyer.polygraph.messagetype.mail.model.Document;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;
import com.illmeyer.polygraph.messagetype.mail.tags.MailTag.MailType;
import com.illmeyer.polygraph.template.PolygraphEnvironment;
import com.illmeyer.polygraph.template.PolygraphTag;
import com.illmeyer.polygraph.template.PolygraphTemplateException;
import com.illmeyer.polygraph.template.TagInfo;
import com.illmeyer.polygraph.template.TagParameter;

@TagInfo(name="resource",nestable=false)
public class ResourceTag implements PolygraphTag {

	public static enum ResourceType {
		attach,
		embed
	}
	
	@TagParameter(optional=true) @Getter
	ResourceType type;
	
	@TagParameter @Getter
	String name;
	
	@TagParameter(name="filename",optional=true) @Getter
	String fileName;
	
	@TagParameter(optional=true) @Getter
	String description;
	
	@TagParameter(name="mimetype") @Getter
	String mimeType;
	
	@Override
	public void execute(PolygraphEnvironment env) throws IOException {
		MailTag m=env.requireAncestorTag(MailTag.class);
		if (m.getType()==MailType.simple) {
			if (type==null)
				throw new PolygraphTemplateException("must specify type attribute below mail tags of type 'simple'");
			env.requireParentTag(MailTag.class);
		}
		if (type==ResourceType.attach && fileName==null)
			throw new PolygraphTemplateException("attached resources need a filename");
		Document d = new Document();
		if (type!=null) d.setDisposition(type.toString());
		d.setMimeType(mimeType);
		d.setPartname(name);
		d.setFilename(fileName);
		if(type==ResourceType.embed) {
			d.setContentId(MailEnvironment.createContentId());
			m.getCidMap().put(d.getPartname(), d.getContentId());
		}
		((MimeBody)m.getPartStack().peek()).getSubElements().add(d);

		m.getPartStack().push(d);
		try {
			if (env.hasBody())
				env.executeBody(NullWriter.NULL_WRITER);
		} finally {
			m.getPartStack().pop();
		}
	}

}
