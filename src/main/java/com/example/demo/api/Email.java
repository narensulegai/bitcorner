package com.example.demo.api;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {
	 static void sendmail(String to, String message) throws Exception {
		   Properties props = new Properties();
		   props.put("mail.smtp.auth", "true");
		   props.put("mail.smtp.starttls.enable", "true");
		   props.put("mail.smtp.host", "smtp.gmail.com");
		   props.put("mail.smtp.port", "587");
		   
		   Session session = Session.getInstance(props, new javax.mail.Authenticator() {
		      protected PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication("", "");
		      }
		   });
		   Message msg = new MimeMessage(session);
		   msg.setFrom(new InternetAddress("singhalakash.bvcoe@gmail.com", false));

		   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		   msg.setSubject("bitcorner update");
		   msg.setContent(message, "text/html");
		   msg.setSentDate(new Date());

		
		   Transport.send(msg);   
		}

}
