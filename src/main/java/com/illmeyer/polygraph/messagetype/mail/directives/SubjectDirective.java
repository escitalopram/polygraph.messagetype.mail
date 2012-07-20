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
import java.io.StringWriter;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.messagetype.mail.MailEnvironment;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class SubjectDirective implements TemplateDirectiveModel {

	private static final String PVALUE="value";
	
	@Override
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map rawparams, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		Map<String,Object> params = rawparams;
		checkPreconditions(env);
		String subject = processSubject(env,body,params);
		((MailDescription)env.getCustomAttribute(MailConstants.ECA_MAILDESC)).setSubject(subject);
	}

	private String processSubject(Environment env, TemplateDirectiveBody body,
			Map<String, Object> params) throws TemplateException, IOException {
		Map<String,String> result = new HashMap<String, String>();
		MailEnvironment.registerParamStringValue(params, PVALUE, result);
		if (result.containsKey(PVALUE)) return result.get(PVALUE);
		if (body==null) throw new TemplateException("subject requires a value",env);
		MailEnvironment.getTagStack(env).push(MailConstants.TAG_SUBJECT);
		try {
			StringWriter sw = new StringWriter();
			body.render(sw);
			String wresult = sw.toString();
			if (wresult==null || wresult.trim().isEmpty())
				throw new TemplateException("subject requires a value",env);
			return wresult;
		} finally {
			MailEnvironment.getTagStack(env).pop();
		}
	}

	private void checkPreconditions(Environment env) throws TemplateException {
		try {
		String topTag = MailEnvironment.getTagStack(env).peek();
		if (MailConstants.TAG_MAIL.equals(topTag))
			return;
		} catch (EmptyStackException e) {
		}
		throw new TemplateException("subject not allowed here",env);
	}

}
