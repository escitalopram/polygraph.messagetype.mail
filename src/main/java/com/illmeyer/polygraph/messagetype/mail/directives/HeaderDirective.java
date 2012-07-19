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
import java.util.HashMap;
import java.util.Map;

import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.messagetype.mail.MailEnvironment;
import com.illmeyer.polygraph.messagetype.mail.model.Body;
import com.illmeyer.polygraph.messagetype.mail.model.HeaderEdit;
import com.illmeyer.polygraph.messagetype.mail.model.HeaderEdit.Operation;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class HeaderDirective implements TemplateDirectiveModel {

	private static final String PNAME="name";
	private static final String POPERATION="operation";
	private static final String PVALUE="value";
	
	@Override
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map rawparams, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		@SuppressWarnings("unchecked")
		Map<String,Object> params = rawparams;
		checkPreconditions(env);
		Map<String,String> p = processParameters(params,env,body);
		Body b = MailEnvironment.getPartStack(env).peek();
		
		HeaderEdit he = new HeaderEdit(p.get(PNAME), p.get(PVALUE), Operation.valueOf(p.get(POPERATION)));
		b.getHeaders().add(he);
	}

	private Map<String, String> processParameters(Map<String, Object> params,
			Environment env, TemplateDirectiveBody body) throws TemplateException, IOException {
		Map<String,String> result = new HashMap<>();
		MailEnvironment.registerParamStringValue(params, PNAME, result);
		MailEnvironment.registerParamStringValue(params, PVALUE, result);
		MailEnvironment.registerParamStringValue(params, POPERATION, result);
		if (result.get(PNAME)==null)
			throw new TemplateException("header name required",env);
		if (result.get(POPERATION)==null)
			result.put(POPERATION, Operation.add.toString());
		try {
			Operation.valueOf(result.get(POPERATION));
		} catch (IllegalArgumentException e) {
			throw new TemplateException("illegal value for operation attribute",env);
		}
		if (Operation.valueOf(result.get(POPERATION))!=Operation.remove && 
				(result.get(PVALUE)==null || result.get(PVALUE).trim().isEmpty())) {
			if (body==null)
				throw new TemplateException("value or body required",env);
			MailEnvironment.getTagStack(env).push(MailConstants.TAG_HEADER);
			try {
				StringWriter sw = new StringWriter();
				body.render(sw);
				String wresult = sw.toString();
				if (wresult==null || wresult.trim().isEmpty())
					throw new TemplateException("directive body is empty",env);
				result.put(PVALUE,wresult);
			} finally {
				MailEnvironment.getTagStack(env).pop();
			}
		}
		return result;
	}

	private void checkPreconditions(Environment env) throws TemplateException {
		if (env.getCustomAttribute(MailConstants.ECA_INMAIL)==null)
			throw new TemplateException("header is only allowed inside a mail directive",env);
		String topTag=MailEnvironment.getTagStack(env).peek();
		if (!MailConstants.TAG_MAIL.equals(topTag)
				&& !MailConstants.TAG_MIME.equals(topTag)
				&& !MailConstants.TAG_RESOURCE.equals(topTag))
			throw new TemplateException("header not allowed here",env);
	}

}
