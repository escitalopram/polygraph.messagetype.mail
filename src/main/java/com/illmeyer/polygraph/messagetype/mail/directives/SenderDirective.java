package com.illmeyer.polygraph.messagetype.mail.directives;

import com.illmeyer.polygraph.messagetype.mail.model.MailAddress;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;

public class SenderDirective extends AbstractAddressDirective {

	@Override
	protected String getTagName() {
		return "sender";
	}

	@Override
	protected boolean useName() {
		return true;
	}

	@Override
	protected void executeDirective(MailDescription desc, MailAddress address) {
		desc.setSender(address);
	}

}
