package me.ejpark.demoinflearnrestapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	// ModelMapper (공용) Bean 등록
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();

	}

}
