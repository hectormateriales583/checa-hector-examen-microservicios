package com.tecsup.biblioteca.librosservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LibrosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrosServiceApplication.class, args);
    }
}
