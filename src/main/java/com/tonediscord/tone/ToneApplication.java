package com.tonediscord.tone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ToneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToneApplication.class, args);
    }
}
