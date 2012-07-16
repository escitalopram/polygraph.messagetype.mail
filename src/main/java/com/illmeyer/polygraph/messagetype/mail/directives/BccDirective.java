package com.illmeyer.polygraph.messagetype.mail.directives;

import com.illmeyer.polygraph.messagetype.mail.model.MailAddress;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;

public class BccDirective extends AbstractAddressDirective {

	@Override
	protected String getTagName() {
		return "bcc";
	}

	@Override
	protected boolean useName() {
		return true;
	}

	@Override
	protected void executeDirective(MailDescription desc, MailAddress address) {
		desc.getBcc().add(address);
	}

}
