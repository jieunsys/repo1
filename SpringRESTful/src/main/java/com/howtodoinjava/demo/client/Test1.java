package com.howtodoinjava.demo.client;

import org.springframework.web.client.RestTemplate;

import com.howtodoinjava.demo.model.EmployeeListVO;
import com.howtodoinjava.demo.model.EmployeeVO;

public class Test1 {

	public static void main(String[] args) {
		new Test1().test1();
	}

	public void test1() {
	    final String uri = "http://localhost:8080/employees";
	    RestTemplate restTemplate = new RestTemplate();
	     
	    EmployeeListVO vos = restTemplate.getForObject(uri, EmployeeListVO.class);

	    for(int i=0;i<vos.getEmployees().size();i++) {
		    EmployeeVO vo = vos.getEmployees().get(i);
		    System.out.println(vo.toString());
	    }
	}
}
