package com.smart.entity;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class MyOrder {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long myOrderId;
	
	
	private String orderId;
	
	private String amount;
	
	
	private String receipt;
	
	
	private String status;
	
	@ManyToOne
	private User user;
	
	private String paymentId;
	
	
	
	

}
