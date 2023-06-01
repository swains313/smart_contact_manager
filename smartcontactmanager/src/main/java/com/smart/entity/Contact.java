package com.smart.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CONTACT")
@Data
public class Contact{
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	
	private String name;
	private String secondName;
	private String work;
	
	@Column(unique = true)
	private String email;
	private String phone;
	private String image;
	@Column(length = 50000)
	private String description;
	
	
	@ManyToOne
	private User user;
	
	
	



	
	
	
	

}
