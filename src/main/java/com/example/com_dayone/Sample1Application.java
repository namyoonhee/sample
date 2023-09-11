package com.example.com_dayone;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Sample1Application {

    public static void main(String[] args) {
        SpringApplication.run(Sample1Application.class, args);
    }

}
