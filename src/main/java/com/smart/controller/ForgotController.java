package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private EmailService emailService;
	Random random = new Random(1000);
	

	@GetMapping("/forgot")
	public String openEmailForm() {
		return "normal/forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {
		System.out.println(email);
		int otp = random.nextInt(9999);
		System.out.println(otp);
		
		String html = ""+"<h1 style='color:red;border:1px solid red;'>Enter this OTP - <b>"+otp+"</b></h1>"+"";
		emailService.sendEmailWithHtml(email, "Smart-Contact-Manager",html);
		session.setAttribute("email", email);
		session.setAttribute("myOtp", otp);
		session.setAttribute("message", new Message("We have sent an OTP.","success"));
		return "verify_otp";
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp, HttpSession session) {
		int myOtp = (Integer) session.getAttribute("myOtp");
		String email = (String) session.getAttribute("email");
		if(myOtp == otp ) {
			User user = userRepo.getUserByUserName(email);
			if(user==null) {
				session.setAttribute("message2", new Message("User does not exist.","danger"));
				return "/normal/forgot_email_form";
			}
			session.setAttribute("email", email);
			return "normal/password_change_form";
		}else {
			session.setAttribute("message2", new Message("You entered wrong OTP.","danger"));
			return "verify_otp";
		}
	}
	
	@PostMapping("/new-password")
	public String newPassword(@RequestParam("password")String password, @RequestParam("email")String email) {
		User user = userRepo.getUserByUserName(email);
		user.setPassword(passwordEncoder.encode(password));
		userRepo.save(user);
		System.out.println(password +"reset");
		return "login";
	}
}
