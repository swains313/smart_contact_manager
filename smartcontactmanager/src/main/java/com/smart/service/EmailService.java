package com.smart.service;


import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {
	
//	public boolean sendEmail(String subject, String message, String to)
//	{
//		
//		boolean b=false;
//		
//		String from="communitydevloper@gmail.com";
//		
//		String host="smtp.gmail.com";
//		
//		//get System properties
//		Properties properties=System.getProperties();
//		System.out.println("Properties "+properties);
//		
//		
//		//host set
//		
//		properties.put("mail.smtp.host", host);
//		properties.put("mail.smtp.port", "587");
//		properties.put("mail.smtp.ssl.enable", "true");
//		properties.put("mail.smtp.smtp.auth", "true");
//		
//		//step 1 to get the session object
//
//		Session session=Session.getInstance(properties, new Authenticator() {
//			protected PasswordAuthentication gPasswordAuthentication()
//			{
//				return new PasswordAuthentication("communitydevloper@gmail.com", "srsaumya89");
//			}
//			
//		});
//		
//		session.setDebug(true);
//		
//		
//		//step2 : compose the message[text, multi media]
//		MimeMessage mimeMessage=new MimeMessage(session);
//		try {
//			//from email
//			mimeMessage.setFrom(from);
//			
//			
//			//adding recipient to message
//			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress());
//			
//			//adding subject to message
//			mimeMessage.setSubject(subject);
//			
//			
//			//adding text to message
//			mimeMessage.setText(message);
//			
//			
//			//send
//			
//			
//			
//			//step 3 : send message using transport class
//			Transport.send(mimeMessage);
//			
//			System.out.println("send seccess....................");
//			b=true;
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		
//		return b;
//		
//		
//	}
//	
	
	
	@Autowired
	private JavaMailSender mailSender;


	@Async
	public boolean sendEmail(SimpleMailMessage email) {
		boolean b=false;

		try {
			mailSender.send(email);
			b=true;
			
		} catch (Exception e) {
			System.out.println("EMAIL ERROR");
		}
	return b;
		
	}
//	
	
	

}
