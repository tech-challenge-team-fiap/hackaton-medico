package com.fiap.hackathon.medico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.fiap.hackathon.medico"})
@EntityScan(basePackages = "com.fiap.hackathon.medico.domain.entity")
public class FiapHackathonMedicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiapHackathonMedicoApplication.class, args);
	}

}
