package com.jadajabajava;

import com.jadajabajava.providers.HhVacancyProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class JobSeekerService {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(JobSeekerService.class, args);

        ctx.getBean(HhVacancyProvider.class).fetchVacancies();
    }
}
