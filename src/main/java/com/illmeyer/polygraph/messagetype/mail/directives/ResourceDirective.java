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
import com.illmeyer.polygraph.messagetype.mail.model.Document;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Deprecated
public class ResourceDirective implements TemplateDirectiveModel {

	private static final String PTYPE="type";
	private static final String PNAME="name";
	private static final String PFILENAME="filename";
	private static final String PDESCRIPTION="description";
	private static final String PMIMETYPE="mimetype";

	private static final String RT_ATTACH="attach";
	private static final String RT_EMBED="embed";
	
	@Override
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map rawparams, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		@SuppressWarnings("unchecked")
		Map<String,Object> params = (Map<String,Object>)rawparams;
		checkPreconditions(env);
		Map<String,String> p = processParams(params,env);
		Document d = new Document();
		d.setDisposition(p.get(PTYPE));
		d.setMimeType(p.get(PMIMETYPE));
		d.setPartname(p.get(PNAME));
		d.setFilename(p.get(PFILENAME));
		if (RT_EMBED.equals(p.get(PTYPE))) {
			d.setContentId(MailEnvironment.createContentId());
			MailEnvironment.getCidMap(env).put(d.getPartname(), d.getContentId());
		}
		((MimeBody)MailEnvironment.getPartStack(env).peek()).getSubElements().add(d);
		MailEnvironment.getTagStack(env).push(MailConstants.TAG_RESOURCE);
		MailEnvironment.getPartStack(env).push(d);
		try {
			if (body!=null)
				body.render(NullWriter.NULL_WRITER);
		} finally {
			MailEnvironment.getTagStack(env).pop();
			MailEnvironment.getPartStack(env).pop();
		}
	}

	private Map<String, String> processParams(Map<String, Object> params, Environment env) throws TemplateException {
		Map<String,String> result = new HashMap<>();
		MailEnvironment.registerParamStringValue(params, PTYPE, result);
		MailEnvironment.registerParamStringValue(params, PNAME, result);
		MailEnvironment.registerParamStringValue(params, PFILENAME, result);
		MailEnvironment.registerParamStringValue(params, PDESCRIPTION, result);
		MailEnvironment.registerParamStringValue(params, PMIMETYPE, result);
		String restype = result.get(PTYPE);
		if (result.get(PNAME)==null)
			throw new TemplateException("'name' attribute required",env);
		if (result.get(PMIMETYPE)==null)
			throw new TemplateException("'mimetype' attribute required",env);
		if (restype==null) {
			if (env.getCustomAttribute(MailConstants.ECA_MAIL_TYPE).equals(MailConstants.MTYPE_SIMPLE))
				throw new TemplateException("'type' attribute required for simple mails",env);
		} else {
			if (restype.equals(RT_ATTACH)) {
				if (result.get(PFILENAME)==null)
					throw new TemplateException("filename must be specified for attached resources",env);
			} else if (restype.equals(RT_EMBED)) {
			} else {
				throw new TemplateException("illegal value for 'type' attribute",env);
			}
		}
		return result;
	}

	private void checkPreconditions(Environment env) throws TemplateException {
		try {
			Body currentBody = MailEnvironment.getPartStack(env).peek();
			if (!(currentBody instanceof MimeBody)) throw new Exception();
			String currentTag = MailEnvironment.getTagStack(env).peek();
			String mailType  = (String) env.getCustomAttribute(MailConstants.ECA_MAIL_TYPE);
			if (mailType.equals(MailConstants.MTYPE_SIMPLE) && currentTag.equals(MailConstants.TAG_MAIL))
				return;
			if (mailType.equals(MailConstants.MTYPE_MIME)
					&& (currentTag.equals(MailConstants.TAG_MAIL) || currentTag.equals(MailConstants.TAG_MIME)))
				return;
		} catch (Exception e) {
		}
		throw new TemplateException("resource directive not allowed here",env);
	}

}
