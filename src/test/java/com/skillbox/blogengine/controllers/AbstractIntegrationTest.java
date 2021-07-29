package com.skillbox.blogengine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
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
     * Create mock mvc.
     */
    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .build();
    }

}