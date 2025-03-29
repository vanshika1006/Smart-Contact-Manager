package com.smart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.smart.services.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{

    private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
	
    @Autowired
	private JavaMailSender mailSender;
	
	
	public EmailServiceImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	@Override
	public void sendEmail(String to, String subject, String message) {
		// TODO Auto-generated method stub
		SimpleMailMessage simple = new SimpleMailMessage();
		simple.setTo(to);
		simple.setSubject(subject);
		simple.setText(message);
		simple.setFrom("vanshikalearnin@gmail.com");
		
		mailSender.send(simple);
		logger.info("Email Sent");
	}

	@Override
	public void sendEmailWithHtml(String to, String subject, String htmlContent) {
		// TODO Auto-generated method stub
		MimeMessage simple = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(simple,true,"UTF-8");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent,true);
			helper.setFrom("vanshikalearnin@gmail.com");
			mailSender.send(simple);
			logger.info("Email Sent");
		} catch(MessagingException e) {
			e.printStackTrace();
		}
	}

}
