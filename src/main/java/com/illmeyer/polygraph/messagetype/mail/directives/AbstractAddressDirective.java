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
import java.util.Map;

import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.messagetype.mail.model.MailAddress;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

@Deprecated
public abstract class AbstractAddressDirective implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		String email=null;
		String name=null;
		final Object oEmail=params.get("email");
		final Object oName=params.get("name");
		if (oName!=null) {
			if (!useName()) throw new TemplateException(getTagName()+" does not allow name attribute",env);
			if (oName instanceof TemplateScalarModel)
				name=((TemplateScalarModel)oName).getAsString();
			else
				throw new TemplateException("need a string for a name",env);
		}
		if (oEmail!=null) {
			if (oEmail instanceof TemplateScalarModel)
				email=((TemplateScalarModel)oEmail).getAsString();
		}
		// TODO possibly check email address validity
		if (email==null || email.trim().isEmpty())
			throw new TemplateException("need a valid email address",env);
		MailAddress addr = new MailAddress(name, email);
		MailDescription md = (MailDescription)env.getCustomAttribute(MailConstants.ECA_MAILDESC);
		executeDirective(md, addr);
	}
	protected abstract String getTagName();
	protected abstract boolean useName();
	protected abstract void executeDirective(MailDescription desc, MailAddress address);

}
