package com.illmeyer.polygraph.messagetype.mail.directives;

import com.illmeyer.polygraph.messagetype.mail.model.MailAddress;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;

public class RcptDirective extends AbstractAddressDirective {

	@Override
	protected String getTagName() {
		return "rcpt";
	}

	@Override
	protected boolean useName() {
		return false;
	}

	@Override
	protected void executeDirective(MailDescription desc, MailAddress address) {
		desc.getRecipients().add(address.getEmail());
	}

}
