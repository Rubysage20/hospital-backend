package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.hospital")
@EnableMongoRepositories(basePackages = "com.hospital.repository")
public class HospitalApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);
        System.out.println("=========================================");
        System.out.println("Hospital API Running!");
        System.out.println("URL: http://localhost:8080/api");
        System.out.println("=========================================");
    }
}
