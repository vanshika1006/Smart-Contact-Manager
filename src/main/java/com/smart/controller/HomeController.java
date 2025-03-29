package com.smart.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result, @RequestParam(value="agreement",defaultValue="false")boolean agreement, Model model) {
        try {
        	if(!agreement) {
            	System.out.println("You have not agreed terms and conditions");
            	throw new Exception("You have not agreed terms and conditions");
            }
        	
        	if (result.hasErrors()) {
        	    result.getFieldErrors().forEach(error -> System.out.println(error.getField() + ": " + error.getDefaultMessage()));
        	    model.addAttribute("user", user);
        	    return "signup";
        	}

        	
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
         
            User res = userRepo.save(user);
            model.addAttribute("user", new User());
            model.addAttribute("message", new Message("Successfully Registered !!","alert-success"));
            return "signup";
        }catch(Exception e) {
        	e.printStackTrace();
        	model.addAttribute("user",user);
        	model.addAttribute("message", new Message("Something went wrong","alert-danger"));
        	return "signup";
        }
	}
	
	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","Login Page");
		return "login";
	}

}
