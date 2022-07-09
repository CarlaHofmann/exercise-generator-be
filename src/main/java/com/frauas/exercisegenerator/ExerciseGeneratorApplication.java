package com.frauas.exercisegenerator;

import java.nio.charset.StandardCharsets;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.frauas.exercisegenerator.helpers.handlebars.IncrementHelper;
import com.frauas.exercisegenerator.mongo.CascadeSaveMongoEventListener;
import com.frauas.exercisegenerator.mongo.DeleteExerciseListener;
import com.frauas.exercisegenerator.mongo.ExerciseVirtualFieldListener;
import com.frauas.exercisegenerator.mongo.SaveSheetListener;
import com.frauas.exercisegenerator.mongo.UpsertSaveMongoEventListener;
import com.frauas.exercisegenerator.util.TokenUtil;
import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
public class ExerciseGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExerciseGeneratorApplication.class, args);
    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
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
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    TokenUtil tokenUtil() {
        TokenUtil tokenUtil = new TokenUtil();
        return tokenUtil;
    }

    @Bean
    Handlebars handlebars() {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");

        Handlebars handlebars = new Handlebars(loader)
                .setCharset(StandardCharsets.UTF_8)
                .prettyPrint(true)
                .with(EscapingStrategy.NOOP)
                .registerHelper(IncrementHelper.NAME, new IncrementHelper());

        return handlebars;
    }

    @Bean
    ExerciseVirtualFieldListener exerciseVirtualFieldListener() {
        return new ExerciseVirtualFieldListener();
    }

    @Bean
    CascadeSaveMongoEventListener cascadeSaveMongoEventListener() {
        return new CascadeSaveMongoEventListener();
    }

    @Bean
    UpsertSaveMongoEventListener upsertSaveMongoEventListener() {
        return new UpsertSaveMongoEventListener();
    }

    @Bean
    DeleteExerciseListener deleteExerciseListener() {
        return new DeleteExerciseListener();
    }

    @Bean
    SaveSheetListener saveSheetListener() {
        return new SaveSheetListener();
    }

    @Configuration
    @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
    @SecurityScheme(name = "refreshToken", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
    public class OpenApi30Config {
    }
}
