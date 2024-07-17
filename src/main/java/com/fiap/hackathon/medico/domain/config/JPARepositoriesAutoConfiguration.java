package com.fiap.hackathon.medico.domain.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("com.fiap.hackathon.medico.domain.repositories")
public class JPARepositoriesAutoConfiguration {

}
