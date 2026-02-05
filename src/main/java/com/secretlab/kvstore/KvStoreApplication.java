package com.secretlab.kvstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class KvStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(KvStoreApplication.class, args);
    }
}
