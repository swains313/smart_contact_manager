package com.smart.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;



@Controller
public class HomeController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@RequestMapping("/")
	public  String home(Model model)
	{
		model.addAttribute("title", "smart contact manager");
		return "home";
	}
	
	
	@RequestMapping("/about")
	public  String about(Model model)
	{
		model.addAttribute("title", " about smart contact manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public  String signUp(Model model)
	{
		model.addAttribute("title", " register smart contact manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	//this handler for register user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
			@RequestParam(value = "agreement",defaultValue = "false")Boolean agreement, 
			Model model, HttpSession httpSession)
	{
		try {
			
			if(!agreement)
			{
				System.out.println("You have not agreed term & condition");
				throw new Exception("You have not agreed term & condition");
			}
			
			if(bindingResult.hasErrors())
			{
				System.out.println("error "+bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImgUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			User result=this.userRepository.save(user);
			//model.addAttribute("user",result);
			
			model.addAttribute("user",new User());
			
			httpSession.setAttribute("message", new Message("Successfully Register", "alert-success"));
			return "signup";
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			
			model.addAttribute("user",user);
			
			httpSession.setAttribute("message", new Message("Something went wrong!!!", "alert-danger"));
			return "signup";
		}
		
	
		

	}
	
	
	
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title", "Log in page");
		return "login";
	}
	
	@GetMapping("/login_failed")
	public String logInFail()
	{
		return "login_fail";
	}


}
