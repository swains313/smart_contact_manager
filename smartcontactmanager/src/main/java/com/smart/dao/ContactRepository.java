package com.smart.dao;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Contact;
import com.smart.entity.User;

public interface ContactRepository  extends JpaRepository<Contact, Integer>{
	
	//pagination...
	//currentpage -page
	//contact per page- 5
	@Query("from Contact c where c.user.id = :userId")
	public Page<Contact> findContactsByUser(@Param("userId") Integer userId, Pageable pageable);
	
	//for search
	public List<Contact> findByNameContainingAndUser(String name,User user);


}
