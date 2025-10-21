package org.example.postsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PostsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostsDemoApplication.class, args);
	}

    // ❯ curl "http://localhost:8080/api/posts?userId=1" | jq '.[] | select(.id == 10)'
}