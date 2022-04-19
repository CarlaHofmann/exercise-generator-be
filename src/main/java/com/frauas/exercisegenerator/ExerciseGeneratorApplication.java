package com.frauas.exercisegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ExerciseGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExerciseGeneratorApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry corsRegistry) {
				corsRegistry.addMapping("/**")
						.allowedOrigins("*")
						.allowedHeaders("*")
						.allowedMethods("*");
			}
		};
	}
}
