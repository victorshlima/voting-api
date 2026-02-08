package com.challenge.voting_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class VotingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingApiApplication.class, args);
	}

}
