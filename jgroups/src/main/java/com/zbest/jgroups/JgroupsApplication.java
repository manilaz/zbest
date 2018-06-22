package com.zbest.jgroups;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class JgroupsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JgroupsApplication.class, args);
	}
}
