package com.vaccine.notify.notifier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
	
	@Autowired
	private JavaMailSender emailSender;
	
	public void sendEmail(String toMail, String content, String subject) {
		
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(toMail);
		mail.setSubject(subject);
		mail.setText(content);
		mail.setFrom("ashikpkmadhupam@gmail.com");
		emailSender.send(mail);
		
	}
}
