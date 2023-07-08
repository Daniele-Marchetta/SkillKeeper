package com.registroformazione;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.auditing.DateTimeProvider;
import java.time.OffsetDateTime;
import java.util.Optional;



@SpringBootApplication
// permette l'utilizzo di @createddate e @updateddate nell'entità excel import
public class RegistroFormazioneApplication {
	//test

	public static void main(String[] args) {
		SpringApplication.run(RegistroFormazioneApplication.class, args);
	}
	
	@Bean
	 ModelMapper modelMapper() {
		return new ModelMapper() ;
	}
	
	
	
}
