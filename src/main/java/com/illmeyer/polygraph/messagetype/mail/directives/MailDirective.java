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
import java.util.Stack;

import org.apache.commons.io.output.NullWriter;
import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.messagetype.mail.MailEnvironment;
import com.illmeyer.polygraph.messagetype.mail.model.Body;
import com.illmeyer.polygraph.messagetype.mail.model.Document;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Deprecated
public class MailDirective implements TemplateDirectiveModel {

	public static final String PTYPE="type";
	public static final String PSUBTYPE="subtype";
	public static final String PTEXTNAME="textname";
	public static final String PHTMLNAME="htmlname";
	
	@Override
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map rawparams, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		@SuppressWarnings("unchecked")
		Map<String,Object> params = rawparams;
		checkContainment(env);
		Map<String,String> p = processParameters(params,env);
		MailDescription md = new MailDescription();
		Stack<Body> partStack = MailEnvironment.getPartStack(env);
		Stack<String> tagStack = MailEnvironment.getTagStack(env);
		tagStack.push(MailConstants.TAG_MAIL);
		env.setCustomAttribute(MailConstants.ECA_INMAIL, Boolean.TRUE);
		env.setCustomAttribute(MailConstants.ECA_MAILDESC, md);
		try {
			if (p.get(PTYPE).equals(MailConstants.MTYPE_SIMPLE)) {
				env.setCustomAttribute(MailConstants.ECA_MAIL_TYPE, MailConstants.MTYPE_SIMPLE);
				MimeBody mainBody = new MimeBody();
				mainBody.setSubType("related");
				partStack.push(mainBody);
				md.setRootElement(mainBody);
				if (p.containsKey(PHTMLNAME) && p.containsKey(PTEXTNAME)) {
					MimeBody alternative = new MimeBody();
					alternative.setSubType("alternative");
					partStack.push(alternative);
					mainBody.getSubElements().add(alternative);
				}
				if(p.containsKey(PTEXTNAME)) {
					Document d = new Document();
					d.setMimeType("text/plain");
					d.setPartname(p.get(PTEXTNAME));
					((MimeBody)partStack.peek()).getSubElements().add(d);
				}
				if(p.containsKey(PHTMLNAME)) {
					Document d = new Document();
					d.setMimeType("text/html");
					d.setPartname(p.get(PHTMLNAME));
					((MimeBody)partStack.peek()).getSubElements().add(d);
				}
				if (p.containsKey(PHTMLNAME) && p.containsKey(PTEXTNAME))
					partStack.pop();
				if (body!=null)
					body.render(NullWriter.NULL_WRITER);
				if (mainBody.getSubElements().size()==1) {
					md.setRootElement(mainBody.getSubElements().get(0));
				}
			} else {
				env.setCustomAttribute(MailConstants.ECA_MAIL_TYPE, MailConstants.MTYPE_MIME);
				MimeBody mainBody = new MimeBody();
				mainBody.setSubType(p.get(PSUBTYPE));
				md.setRootElement(mainBody);
				partStack.push(mainBody);
				if (body==null) throw new TemplateException("'mime' type mail directive requires a body",env);
				body.render(NullWriter.NULL_WRITER);
			}
		} finally {
			env.removeCustomAttribute(MailConstants.ECA_INMAIL);
			env.removeCustomAttribute(MailConstants.ECA_PART_STACK);
			env.removeCustomAttribute(MailConstants.ECA_TAGSTACK);
		}
	}


	private Map<String,String> processParameters(Map<String, Object> params, Environment env) throws TemplateException {
		Map<String,String> result = new HashMap<>();
		MailEnvironment.registerParamStringValue(params, PTYPE, result);
		MailEnvironment.registerParamStringValue(params, PSUBTYPE, result);
		MailEnvironment.registerParamStringValue(params, PTEXTNAME, result);
		MailEnvironment.registerParamStringValue(params, PHTMLNAME, result);
		if (result.get(PTYPE)==null) throw new TemplateException("mail directive must have a type attribute",env);
		if (result.get(PTYPE).equals(MailConstants.MTYPE_MIME)) {
			if (!result.containsKey(PSUBTYPE))
				throw new TemplateException("mime type mails require 'subtype' attribute",env);
			if (result.containsKey(PTEXTNAME) || result.containsKey(PHTMLNAME))
				throw new TemplateException("mime type mails do not allow 'textname' or 'htmlname' attributes",env);
		} else if (result.get(PTYPE).equals(MailConstants.MTYPE_SIMPLE)) {
			if (result.containsKey(PSUBTYPE))
				throw new TemplateException("simple type mails do not allow 'subtype' attribute",env);
			if (!result.containsKey(PTEXTNAME) && !result.containsKey(PHTMLNAME))
				throw new TemplateException("mime type mails require at least one of the  'textname' or 'htmlname' attributes",env);
		} else {
			throw new TemplateException("type must be 'simple' or 'mime'",env);
		}
		return result;
	}
	
	private void checkContainment(Environment env) throws TemplateException {
		if (env.getCustomAttribute(MailConstants.ECA_INMAIL)!=null)
			throw new TemplateException("mail tags cannot be nested",env);
		if (env.getCustomAttribute(MailConstants.ECA_MAILDESC)!=null)
			throw new TemplateException("a mail is already defined", env);
	}
}
