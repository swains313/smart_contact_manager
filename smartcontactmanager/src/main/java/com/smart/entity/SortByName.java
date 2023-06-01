package com.smart.entity;

import java.util.Comparator;

public class SortByName implements Comparator<Object>{
	
	@Override
	public int compare(Object o1, Object o2) {
		Contact u1=(Contact)o1;
		Contact u2=(Contact)o2;
		String n1=u1.getName();
		String n2=u2.getName();
		return n1.compareTo(n2);
	}
	

}
