package com.illmeyer.polygraph.messagetype.mail.directives;

import com.illmeyer.polygraph.messagetype.mail.model.MailAddress;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;

public class FromDirective extends AbstractAddressDirective {

	@Override
	protected String getTagName() {
		return "from";
	}

	@Override
	protected boolean useName() {
		return true;
	}

	@Override
	protected void executeDirective(MailDescription desc, MailAddress address) {
		desc.setFrom(address);
	}

}
