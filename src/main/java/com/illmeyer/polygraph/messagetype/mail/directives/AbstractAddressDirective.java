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
