package com.frauas.exercisegenerator;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.frauas.exercisegenerator.mongo.CascadeSaveMongoEventListener;
import com.frauas.exercisegenerator.mongo.UpsertSaveMongoEventListener;
import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

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

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		return modelMapper;
	}

	@Bean
	public Handlebars handlebars() {
		TemplateLoader loader = new ClassPathTemplateLoader();
		loader.setPrefix("/templates");

		Handlebars handlebars = new Handlebars(loader)
				.prettyPrint(true)
				.with(EscapingStrategy.NOOP);

		return handlebars;
	}

	@Bean
	public CascadeSaveMongoEventListener cascadeSaveMongoEventListener() {
		return new CascadeSaveMongoEventListener();
	}

	@Bean
	public UpsertSaveMongoEventListener upsertSaveMongoEventListener() {
		return new UpsertSaveMongoEventListener();
	}
}
