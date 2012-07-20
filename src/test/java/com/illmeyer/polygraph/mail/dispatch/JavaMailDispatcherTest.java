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

package com.illmeyer.polygraph.mail.dispatch;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.junit.Ignore;
import org.junit.Test;

import com.illmeyer.polygraph.mail.dispatch.JavaMailDispatcher;

@Ignore
public class JavaMailDispatcherTest {
	@Test
	public void testDispatcher() {
		JavaMailDispatcher dis = new JavaMailDispatcher();
		Properties p = new Properties();
		p.setProperty("mail.smtp.host", System.getenv("SMTP_HOST"));
		p.setProperty("mail.transport.protocol", "smtp");
		p.put("mail.smtp.auth", Boolean.TRUE);
		Session s = Session.getInstance(p,new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(System.getenv("SMTP_USER"), System.getenv("SMTP_PASS"));
			}
		});
		dis.setSession(s);
		dis.initialize();
		dis.dispatchMessage(null);
		dis.destroy();
	}
}
