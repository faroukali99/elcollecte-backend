package com.elcollecte.formulaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class FormulaireApplication {
    public static void main(String[] args) {
        SpringApplication.run(FormulaireApplication.class, args);
    }
}
