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
import java.util.Map;

import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.template.BodyExistence;
import com.illmeyer.polygraph.template.PolygraphEnvironment;
import com.illmeyer.polygraph.template.PolygraphTag;
import com.illmeyer.polygraph.template.PolygraphTemplateException;
import com.illmeyer.polygraph.template.TagInfo;
import com.illmeyer.polygraph.template.TagParameter;

@TagInfo(name="linkembedded", body=BodyExistence.FORBIDDEN)
public class LinkEmbeddedTag implements PolygraphTag {

	@TagParameter
	String name;
	
	@Override
	public void execute(PolygraphEnvironment env) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String,String> cidMap = (Map<String, String>) env.getCustomAttribute(MailConstants.ECA_CIDMAP);
		if (cidMap==null)
			throw new PolygraphTemplateException("mail not yet defined, cannot link item");
		String result = cidMap.get(name);
		if (result==null)
			throw new PolygraphTemplateException(String.format("content id '%s' not found",name));
		env.getWriter().append(result);
	}
}
