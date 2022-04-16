package edu.sou.cs452.jlox;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

/*
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        // SpringApplication.run(App.class, args); // part 3 of lab 3???
        System.out.println("Hello world");
    }
    /* // part 3 of lab 3???
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:8000");
            }
        };
    }
    */
}
