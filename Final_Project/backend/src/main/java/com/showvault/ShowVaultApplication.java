package com.showvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@ComponentScan(basePackages = {"com.showvault", "com.showvault.security.services"})
@EntityScan(basePackages = {"com.showvault.models", "com.showvault.model"})
@EnableJpaRepositories(basePackages = "com.showvault.repository")
@EnableScheduling
public class ShowVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowVaultApplication.class, args);
	}

}