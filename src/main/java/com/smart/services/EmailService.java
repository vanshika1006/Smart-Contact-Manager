package com.smart.services;

public interface EmailService {
	void sendEmail(String to, String subject, String message);
	void sendEmailWithHtml(String to, String subject, String htmlContent);
}
