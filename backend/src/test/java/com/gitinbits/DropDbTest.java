package com.gitinbits;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class DropDbTest {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void dropDatabase() {
        mongoTemplate.getDb().drop();
        System.out.println("TEST DROPPED DATABASE SUCCESSFULLY");
    }
}
