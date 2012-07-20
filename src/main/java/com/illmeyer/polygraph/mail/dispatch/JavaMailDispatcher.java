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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import lombok.Data;

import com.illmeyer.polygraph.core.data.Message;
import com.illmeyer.polygraph.core.data.MessagePart;
import com.illmeyer.polygraph.core.spi.MessageDispatcher;
import com.illmeyer.polygraph.messagetype.mail.MailConstants;
import com.illmeyer.polygraph.messagetype.mail.model.Body;
import com.illmeyer.polygraph.messagetype.mail.model.Document;
import com.illmeyer.polygraph.messagetype.mail.model.HeaderEdit;
import com.illmeyer.polygraph.messagetype.mail.model.MailAddress;
import com.illmeyer.polygraph.messagetype.mail.model.MailDescription;
import com.illmeyer.polygraph.messagetype.mail.model.MimeBody;

@Data
public class JavaMailDispatcher implements MessageDispatcher {

	private Session session;
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	private static InternetAddress convert(MailAddress a) throws UnsupportedEncodingException {
		return new InternetAddress(a.getEmail(), a.getName());
	}
	
	private static void setupDocument(Document d, Message info, Part mbp) throws MessagingException {
		if (d.getFilename()!=null)
			mbp.setFileName(d.getFilename());
		if (d.getDisposition() != null) 
			mbp.setDisposition(d.getDisposition()+"ed");
		if (d.getContentId()!=null && mbp instanceof MimeBodyPart) 
			((MimeBodyPart)mbp).setContentID("<"+d.getContentId()+">");
		String mtype = d.getMimeType();
		MessagePart mp = info.getParts().get(d.getPartname());
		if (mp.getEncoding()!=null) 
			mtype+="; charset="+mp.getEncoding();
		byte[] msg = new byte[]{};
		if (mp.getMessage()!=null) msg=mp.getMessage();
		mbp.setDataHandler(new DataHandler(new ByteArrayDataSource(msg, mtype)));
	}
	
	private static void setupHeaders(Body from, Part mbp) throws MessagingException {
		for(HeaderEdit he : from.getHeaders()) {
			try {
				switch (he.getOperation()) {
				case add:
					mbp.addHeader(he.getName(), MimeUtility.encodeWord(he.getContent(), "UTF-8", null));
					break;
				case replace:
					mbp.removeHeader(he.getName());
					mbp.addHeader(he.getName(), MimeUtility.encodeWord(he.getContent(), "UTF-8", null));
					break;
				case remove:
					mbp.removeHeader(he.getName());
					break;
				}
			} catch (Exception e) {
				throw new RuntimeException("never happens",e);
			}
		}
	}
	
	private static MimeBodyPart createBody(Body from, Message info, Part headerPart) throws MessagingException {
		MimeBodyPart mbp = new MimeBodyPart();
		if (from instanceof MimeBody) {
			MimeBody mb = (MimeBody) from;
			MimeMultipart mmp = new MimeMultipart();
			mmp.setSubType(mb.getSubType());
			for (Body p:mb.getSubElements()) {
				mmp.addBodyPart(createBody(p, info,null));
			}
			mbp.setContent(mmp);
			if (headerPart==null)
				setupHeaders(from, mbp);
		} else if (from instanceof Document) {
			Document d = (Document) from;
			setupDocument(d, info, mbp);
			setupHeaders(from, mbp);
		} else {
			throw new RuntimeException("this should never happen");
		}
		return mbp;
	}
	
	private static void createRootPart(MailDescription md, Message info, MimeMessage dest) throws MessagingException {
		Body root = md.getRootElement(); 
		if (root instanceof Document) {
			setupDocument((Document)root, info, dest);
			setupHeaders(root, dest);
		} else if (root instanceof MimeBody) {
			MimeBodyPart x = createBody(root, info,dest);
			try {
				dest.setContent((MimeMultipart)x.getContent());
				setupHeaders(md.getRootElement(), dest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("this should never happen");
		}
	}
	
	public void dispatchMessage(Message m) {
		try {
			MailDescription md = (MailDescription) m.getProperties().get(MailConstants.MP_MAILDESC);

			MimeMessage msg = new MimeMessage(session);
			if (md.getFrom()!=null) msg.setFrom(convert(md.getFrom()));
			if (md.getSender()!=null) msg.setSender(convert(md.getSender()));
			for (MailAddress a:md.getTo()) 
				msg.addRecipient(RecipientType.TO, convert(a));
			for (MailAddress a:md.getCc()) 
				msg.addRecipient(RecipientType.CC, convert(a));
			for (MailAddress a:md.getBcc()) 
				msg.addRecipient(RecipientType.BCC, convert(a));
			Set<Address> rcpts = new HashSet<Address>();
			for (String a:md.getRecipients())
				rcpts.add(new InternetAddress(a));
			if (rcpts.isEmpty()) {
				rcpts.addAll(Arrays.asList(msg.getRecipients(RecipientType.TO)));
				rcpts.addAll(Arrays.asList(msg.getRecipients(RecipientType.CC)));
				rcpts.addAll(Arrays.asList(msg.getRecipients(RecipientType.BCC)));
			}
			if (md.getSubject()!=null) msg.setSubject(md.getSubject());

			createRootPart(md, m, msg);
			
			Transport t =session.getTransport(); 
			t.connect();
			t.sendMessage(msg, rcpts.toArray(new Address[]{}));
			t.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
