package com.drive.keychain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * This is the main class for the Keychain application. It serves as the entry point for the Spring Boot application.
 * The @SpringBootApplication annotation indicates that this is a Spring Boot application and triggers auto-configuration,
 * component scanning, and other features of Spring Boot.
 */

@SpringBootApplication
public class KeychainApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeychainApplication.class, args);
	}

}
