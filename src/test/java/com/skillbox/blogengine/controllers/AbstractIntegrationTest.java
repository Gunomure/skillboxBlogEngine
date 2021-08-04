package com.skillbox.blogengine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbox.blogengine.initializer.Mysql;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.skillbox.blogengine.initializer.Mysql.mysqlContainer;

@SpringBootTest
@ContextConfiguration(initializers = {
        Mysql.Initializer.class
})
//@Testcontainers
@ActiveProfiles("test-containers")
public abstract class AbstractIntegrationTest {
    /**
     * Web application context.
     */
    @Autowired
    protected WebApplicationContext ctx;

    /**
     * Mock mvc.
     */
    protected MockMvc mockMvc;

    /**
     * Object mapper. To convert object to json
     */
    @Autowired
    protected ObjectMapper mapper;

    /**
     * run single container for all tests
     */
    @BeforeAll
    static void init() {
        mysqlContainer.start();
    }

    /**
     * Create mock mvc.
     */
    @BeforeEach
    void initEach() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .build();
    }
}