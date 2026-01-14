package ru.artem.papyan.ragbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RagbotApplication {

    static void main(String[] args) {
        SpringApplication.run(RagbotApplication.class, args);
    }

}
