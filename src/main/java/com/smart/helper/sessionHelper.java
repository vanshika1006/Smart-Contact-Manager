package com.smart.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class sessionHelper {
	public void removeMessage() {
		try {
			System.out.println("in helper");
			HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			session.removeAttribute("message");
			session.removeAttribute("message2");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
