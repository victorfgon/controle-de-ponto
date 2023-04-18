package com.example.controledeponto.config;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MongoConfigTest {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void testMongoClient() {
        assertNotNull(mongoClient);
    }

    @Test
    void testMongoTemplate() {
        assertNotNull(mongoTemplate);
    }
}