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

import org.apache.commons.io.output.NullWriter;

import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.messagetype.mail.MailEnvironment;
import com.illmeyer.polygraph.messagetype.mail.model.Body;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class MimeDirective implements TemplateDirectiveModel {

	private static final String PTYPE = "type";

	@Override
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map rawparams, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		@SuppressWarnings("unchecked")
		Map<String,Object> params = rawparams;
		checkPreconditions(env, body);
		Map<String,String> p = processParameters(params,env);
		MimeBody mb = new MimeBody();
		mb.setSubType(p.get(PTYPE));
		((MimeBody)MailEnvironment.getPartStack(env).peek()).getSubElements().add(mb);
		MailEnvironment.getPartStack(env).push(mb);
		MailEnvironment.getTagStack(env).push(MailConstants.TAG_MIME);
		try {
			body.render(NullWriter.NULL_WRITER);
		} finally {
			MailEnvironment.getPartStack(env).pop();
			MailEnvironment.getTagStack(env).pop();
		}
		checkPostConditions(mb,env);
	}

	private void checkPostConditions(MimeBody mb, Environment env) throws TemplateException {
		if (mb.getSubElements().size()==0)
			throw new TemplateException("mime tag must contain at least one body",env);
	}

	private Map<String,String> processParameters(Map<String, Object> params, Environment env) throws TemplateException {
		Map<String,String> result = new HashMap<>();
		MailEnvironment.registerParamStringValue(params, PTYPE, result);
		if (result.get(PTYPE)==null) throw new TemplateException("type attribute required",env);
		return result;
	}

	private void checkPreconditions(Environment env, TemplateDirectiveBody body) throws TemplateException {
		if (body==null)
			throw new TemplateException("mime tag must contain at least one body",env);
		Body b = MailEnvironment.getPartStack(env).peek();
		if (b==null || !(b instanceof MimeBody)) throw new TemplateException("mime tag not allowed here",env);
		if (((String)env.getCustomAttribute(MailConstants.ECA_MAIL_TYPE)).equals(MailConstants.MTYPE_SIMPLE))
			throw new TemplateException("mime tag not allowed in simple mails",env);
		String currentTag = MailEnvironment.getTagStack(env).peek();
		if (!currentTag.equals(MailConstants.TAG_MIME) && !currentTag.equals(MailConstants.TAG_MAIL))
			throw new TemplateException("mime tag not allowed here",env);
	}

}
