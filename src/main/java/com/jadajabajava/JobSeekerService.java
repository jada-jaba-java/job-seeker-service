package com.jadajabajava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class JobSeekerService {

    public static void main(String[] args) {
        SpringApplication.run(JobSeekerService.class, args);
    }
}
