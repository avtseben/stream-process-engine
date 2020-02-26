package ru.trueengineering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RawLogAddSemanticApplication {
    public static void main(String[] args) {
        SpringApplication.run(RawLogAddSemanticApplication.class, args);
    }
}
