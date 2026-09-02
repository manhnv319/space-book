package com.velstrong.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookBackendHexApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookBackendHexApplication.class, args);
    }
}
