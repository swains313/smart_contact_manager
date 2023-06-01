package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.MyOrder;
import com.smart.entity.SortByName;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@Autowired
	private MyOrderRepository myOrderRepository;
	
	
	
	
	
	
	
	
	

	// method for adding common data to response
	@ModelAttribute
	public void addCommaonData(Model model, Principal principal) {
		// get the username using prinicipal
		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);
		model.addAttribute("user", user);

	}

	// dash board home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		// get the username using prinicipal
//		String username=principal.getName();
//		User user=userRepository.getUserByUserName(username);
//		model.addAttribute("user", user);
		model.addAttribute("title", "User DashBoard");
		return "normal/user_dashboard";
	}

	// open add from handler
	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// process add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute("contact") Contact contact,
			@RequestParam(name = "profileImage") MultipartFile multipartFile, Principal principal,
			HttpSession httpSession) {
		try {
			String usernae = principal.getName();
			User user = this.userRepository.getUserByUserName(usernae);
			// proccessing & upload file
			if (multipartFile.isEmpty()) {
				System.out.println("file is empty");
				
				contact.setImage("pexels-feyza-yıldırım-16407235.jpg");
			} else {
				// upload file to the folder
				contact.setImage(multipartFile.getOriginalFilename());
				File file = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			user.getContacts().add(contact);
			contact.setUser(user);
			this.userRepository.save(user);
			// message success
			httpSession.setAttribute("message", new Message("Your Contact is saved", "success"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			// message error
			httpSession.setAttribute("message", new Message("Not saved", "danger"));
		}

		return "normal/add_contact_form";
	}

	// show contact handler
	// per page 5 contacts
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		String username = principal.getName();

		User user = this.userRepository.getUserByUserName(username);

		// HERE page stands for current page which come from front end part & 5 is how
		// many contact u want to show per page
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> list = this.contactRepository.findContactsByUser(user.getId(), pageable);

//		Collections.sort(Arrays.asList(list), new SortByName());

		model.addAttribute("title", "show contact");
		model.addAttribute("contact", list);
		// it pass the current page
		model.addAttribute("currentpage", page);
		// it pass the total page like if u have 15 contact it make 3 page 15/5=3
		model.addAttribute("totalpages", list.getTotalPages());
		return "normal/show_contacts";
	}
	
	
	
	
	

	// showing particular contact detail
	@GetMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		//
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", "contact detail");
		} else {
			System.err.println("ERROR");
		}

		return "normal/contact_detail";
	}

	// delete contact
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, MultipartFile multipartFile, Model model,
			HttpSession httpSession, Principal principal) throws IOException {
		Optional<Contact> optional = this.contactRepository.findById(cId);
		Contact contact = optional.get();

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		String imgurl = contact.getImage();

		// AVOID TO UNWANTED DELETE THROUGH THE URL
		if (user.getId() == contact.getUser().getId()) {
			this.contactRepository.delete(contact);
			System.out.println("TRUE");
			File file = new ClassPathResource("static/img").getFile();

			// FOR DELETE THE IMAGE
			if (file == null) {
				System.out.println("file is empty");
			} else {
				// upload file to the folder
				// contact.setImage(multipartFile.getOriginalFilename());

				Path path = Paths.get(file.getAbsolutePath() + File.separator + imgurl);
				Files.delete(path);
				this.contactRepository.delete(contact);
			}

		} else {
			System.err.println("ERROR");

		}

		httpSession.setAttribute("message", new Message("contact deleted succesfullt", "success"));

		return "redirect:/user/show-contacts/0";
	}

	// UPDATE FROM HANDLER
	@PostMapping("/update_contact/{cid}")
	public String updateForm(Model model, @PathVariable("cid") Integer cid) {
		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("title", "update form");
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}

	// update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute("contact") Contact contact,
			@RequestParam("profileImage") MultipartFile multipartFile, Model model, HttpSession httpSession,
			Principal principal) {
		model.addAttribute("title", "Updated");
		try {

			// old contact detail
			Contact contact2 = this.contactRepository.findById(contact.getCId()).get();

			if (!multipartFile.isEmpty()) {
				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, contact2.getImage());
				file1.delete();

				// update new photo
				// contact.setImage(multipartFile.getOriginalFilename());
				File file = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(multipartFile.getOriginalFilename());

			} else {
				contact.setImage(contact2.getImage());

			}

			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);

			this.contactRepository.save(contact);

			httpSession.setAttribute("message", new Message("Your contact is updated", "success"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "redirect:/user/contact/" + contact.getCId();
	}
	
	
	
	
	
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title","Your profile");
		return "normal/profile";
		
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String openSettings(Model model)
	{
		model.addAttribute("title","setting");
		return "normal/settings";
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword, Model model,Principal principal,HttpSession httpSession)
	{
		model.addAttribute("title","change password");
		
		User user=this.userRepository.getUserByUserName(principal.getName());
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			//change the password
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			httpSession.setAttribute("message", new Message("Your Password successfully changed", "success"));
			
		}
		else {
			//password does not match
			httpSession.setAttribute("message", new Message("Password Not matched", "danger"));
			return "redirect:/user/settings";
		}
		
		
		
		
		
		
		return "redirect:/user/index";
	}
	
	
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data,Principal principal) throws RazorpayException
	{
		
		
		//System.err.println("ORDER CREATED");
		//System.out.println(data);
		Integer amt=Integer.parseInt(data.get("amount").toString());
		
		
	 var client=new RazorpayClient("rzp_test_y2DFOeojzgACGh", "U7WcVq2WgOwGSxanZazktcNH"); //rzp_test_y2DFOeojzgACGh U7WcVq2WgOwGSxanZazktcNH
		
	 JSONObject jsonObject=new JSONObject();
	 jsonObject.put("amount", amt*100);
	 jsonObject.put("currency", "INR");
	 jsonObject.put("receipt", "txn_56252");
	 
	 
	 //creating new order
	 
	 Order order=client.orders.create(jsonObject);
	 
	// System.out.println(order);
	 
	 
	 
	 //save the order in database
	 String username=principal.getName();

	 MyOrder myOrder=new MyOrder();
	 myOrder.setAmount(order.get("amount")+"");
	 myOrder.setOrderId(order.get("id"));
	 myOrder.setPaymentId(null);
	 myOrder.setStatus("created");
	 myOrder.setUser(userRepository.getUserByUserName(username));
	 myOrder.setReceipt(order.get("receipt"));
	 this.myOrderRepository.save(myOrder);
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 //if you want you can save data in your data base
		
		
		
		return order.toString();
	}
	
	
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data)
	{
		MyOrder myOrder=this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		this.myOrderRepository.save(myOrder);
		
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

//	for remove message thymelef apge
//	public void removeVerificationMessageFromSession() {
//        try {
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            HttpSession session = request.getSession();
//            session.removeAttribute("verificationMessage");
//        } catch (RuntimeException ex) {
//            //Log.error("No Request: ", ex);
//        }
//    }

}
