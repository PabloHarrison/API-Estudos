package com.example.DBEstudosAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DbEstudosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbEstudosApiApplication.class, args);
	}

}
