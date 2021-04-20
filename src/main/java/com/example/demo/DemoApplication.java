package com.example.demo;

import com.google.firebase.FirebaseApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		FirebaseApp.initializeApp();
		SpringApplication.run(DemoApplication.class, args);
	}

}
