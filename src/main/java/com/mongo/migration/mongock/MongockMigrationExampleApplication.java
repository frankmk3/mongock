package com.mongo.migration.mongock;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongock
public class MongockMigrationExampleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MongockMigrationExampleApplication.class, args);
    }

}
