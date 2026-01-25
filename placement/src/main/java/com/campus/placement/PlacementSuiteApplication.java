package com.campus.placement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// The 'exclude' part below is the Key Fix!
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PlacementSuiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlacementSuiteApplication.class, args);
	}

}