package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepository contactRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	//method for adding common data to response.. automatically added
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String name = principal.getName();
		//get the user using username
		User user = userRepo.getUserByUserName(name);
		System.out.println(user);
		model.addAttribute("user",user);
	}
	
	
	//home
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add contact form
	@GetMapping("/add-contact")
	public String addContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	//processing contact form 
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {
		try{
			String name = principal.getName();
			User user = userRepo.getUserByUserName(name);
			
			//processing file
			if(file.isEmpty()) {
				System.out.println("File is empty");
				contact.setImage("contact.png");
			}else {
				contact.setImage(file.getOriginalFilename());
				File saved = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saved.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Uploaded");
			}
			contact.setUser(user);
		
			user.getContacts().add(contact);
			userRepo.save(user);
			System.out.println("Added contact");
			session.setAttribute("message",new Message("Your Contact is added","success"));
			
		}catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message",new Message("Something went wrong! Try Again","danger"));
		}
		return "normal/add_contact_form";
	}
	
	//show contacts
	// per page 5 contacts
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal p, Pageable pageable) {
		m.addAttribute("title","Show Contacts");
		String name = p.getName();
		User user = userRepo.getUserByUserName(name);
		
		Pageable request = PageRequest.of(page, 3);
		Page<Contact> list = contactRepo.findContactsByUser(user.getId(),request);
		
		m.addAttribute("contacts", list);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages",list.getTotalPages());
		
		return "normal/show_contacts";
	}

	
	//showing particular contact details
	@GetMapping("contact/{cId}")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model m, Principal princi) {
		System.out.println(cId);
		Optional<Contact> contactD = contactRepo.findById(cId);
		Contact contact = contactD.get();
		
		String name = princi.getName();
		User user = userRepo.getUserByUserName(name);
		
		if(user.getId()== contact.getUser().getId()) {
			m.addAttribute("title", contact.getName()); 
			m.addAttribute("contact", contact);
		}
		return "normal/contact_detail";
	}
	
	//deleting contact
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId")int cId, Model m, HttpSession session, Principal princi) {
		Optional<Contact> optional = contactRepo.findById(cId);
		Contact contact = optional.get();
		
		User user = userRepo.getUserByUserName(princi.getName());
		user.getContacts().remove(contact);
		contact.setUser(null);
		userRepo.save(user);
		//contactRepo.delete(contact);
		session.setAttribute("message", new Message("Contact deleted successfully !!","success"));
		return "redirect:/user/show-contacts/0";
	}
	
	//updating contact
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId")int cId, Model m) {
		m.addAttribute("title", "Update Contact");
		Optional<Contact> optional = contactRepo.findById(cId);
		Contact contact = optional.get();
		m.addAttribute("contact", contact);
		return "normal/update_contact";
	}
	
	//process update
	@PostMapping("/process-update")
	public String updateHalder(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Model m, HttpSession session, Principal princi) {
		try {
			//fetch old contact detail
			Contact old = contactRepo.findById(contact.getcId()).get();
			if(!file.isEmpty()) {
				//file work
				
				//delete old photo
				File dlt = new ClassPathResource("static/img").getFile();
				File file1 = new File(dlt,old.getImage());
				file1.delete();
				
				//update new photo
				File saved = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saved.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}else {
				contact.setImage(old.getImage());
			}
			String name = princi.getName();
			User user = userRepo.getUserByUserName(name);
			contact.setUser(user);
			
			contactRepo.save(contact);
			session.setAttribute("message", new Message("Your contact is Updated","success"));
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(contact.getName());
		System.out.println(contact.getcId());
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	//your profile
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title","Profile Page");
		return "normal/profile";
	}
	
	//open setting
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}
	
	//change password..handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal princi, HttpSession session) {
		
		String username = princi.getName();
		User user = userRepo.getUserByUserName(username);
		if(passwordEncoder.matches(oldPassword, user.getPassword())) {
			
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepo.save(user);
			session.setAttribute("message",new Message("Your password is successfully changed", "success"));
			
		}else {
			//error
			session.setAttribute("message",new Message("Your old password is wrong", "danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
}
