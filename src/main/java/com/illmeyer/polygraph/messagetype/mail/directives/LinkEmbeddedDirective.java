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

package com.illmeyer.polygraph.messagetype.mail.directives;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.messagetype.mail.MailEnvironment;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Deprecated
public class LinkEmbeddedDirective implements TemplateDirectiveModel {

	private static final String PNAME="name";
	
	@Override
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map rawparams, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		@SuppressWarnings("unchecked")
		Map<String,Object> params=rawparams;
		if (body!=null)
			throw new TemplateException("no body allowed", env);
		if (env.getCustomAttribute(MailConstants.ECA_INMAIL)!=null)
			throw new TemplateException("directive not allowed inside mail directive",env);
		if (env.getCustomAttribute(MailConstants.ECA_MAILDESC)==null)
			throw new TemplateException("mail description not yet defined",env);
		
		Map<String,String> p = new HashMap<>();
		MailEnvironment.registerParamStringValue(params, PNAME, p);
		String name = p.get(PNAME);
		
		if (name==null || name.trim().isEmpty())
			throw new TemplateException("name attribute required",env);
		String result = MailEnvironment.getCidMap(env).get(name);
		if (result==null)
			throw new TemplateException("content id not found",env);
		env.getOut().write("cid:"+result);
	}

}
