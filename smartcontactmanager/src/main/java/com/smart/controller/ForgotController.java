package com.smart.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	Random random=new Random(1000);
	
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	//email id open handler
	
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session,HttpServletResponse response) throws IOException
	{
		System.out.println(email);
		
		//generating otp 4 digit
		int otp=random.nextInt(999999);
		System.out.println(otp);
		//write code to sent otp email
		String subject="OTP From SCM";
		//PrintWriter printWriter=response.getWriter();
		String message="<h1> "+otp+" </h1>";
		String to=email;
		
		
		//MimeMessage message2=new MimeMessage(null);
		//message2.setContent(message, "text/html");
	
		
		
		
		SimpleMailMessage registrationEmail = new SimpleMailMessage();
		registrationEmail.setTo(email);
		registrationEmail.setSubject(subject);
		registrationEmail.setText(message);
		registrationEmail.setFrom("communitydevloper@gmail.com");
		
		boolean flag=this.emailService.sendEmail(registrationEmail);

		//System.out.println(registrationEmail);
		

		//emailService.sendEmail(registrationEmail);
		
		
		
		
		if(flag)
		{
			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
			
		}else {
			session.setAttribute("message", "Wrong Email... Email Id Not Exist!!!");
			return "forgot_email_form";
			
		}
		
		
		
		
	
	}
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession httpSession)
	{
		int otp1=(Integer) httpSession.getAttribute("otp");
		String email=(String) httpSession.getAttribute("email");
		
		if(otp1==otp)
		{
			//password change form
			
			
			User user=this.userRepository.getUserByUserName(email);
			if(user==null)
			{
				//send error message
				httpSession.setAttribute("message", "Wrong Email... Email Id Not Exist!!!");
				return "forgot_email_form";
			}
			else {
				
			}
			
			return "password_change_form";
			
			
		
			
		}else {
			httpSession.setAttribute("message", "You Have Entered Wrong Otp!!!");
			return "verify_otp";
		}
		
		
	}
	
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword")String newPassword,HttpSession httpSession)
	{
		String email=(String) httpSession.getAttribute("email");
		
		User user=this.userRepository.getUserByUserName(email);
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		//httpSession.setAttribute("message", )
		
		return "redirect:/signin?change=password change successfully";
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
