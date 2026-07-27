package com.example.waxing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WaxingApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaxingApplication.class, args);
    }

}
