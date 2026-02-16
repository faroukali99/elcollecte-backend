package com.elcollecte.analytique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class AnalytiqueApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalytiqueApplication.class, args);
    }
}
