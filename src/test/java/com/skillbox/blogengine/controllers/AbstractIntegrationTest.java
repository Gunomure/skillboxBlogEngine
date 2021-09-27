package com.skillbox.blogengine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbox.blogengine.dto.LoginData;
import com.skillbox.blogengine.initializer.Mysql;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static com.skillbox.blogengine.initializer.Mysql.mysqlContainer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(initializers = {
        Mysql.Initializer.class
})
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

    @Mock
    Principal principal;

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

    void loginAsUser() throws Exception {
        when(principal.getName()).thenReturn("test2@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test2@mail.ru");
        loginData.setPassword("qweqwe2");
        String loginJson = mapper.writeValueAsString(loginData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    void loginAsModerator() throws Exception {
        when(principal.getName()).thenReturn("test1@mail.ru");

        LoginData loginData = new LoginData();
        loginData.setEmail("test1@mail.ru");
        loginData.setPassword("qweqwe1");
        String loginJson = mapper.writeValueAsString(loginData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                ).andDo(print())
                .andExpect(status().isOk());
    }
}