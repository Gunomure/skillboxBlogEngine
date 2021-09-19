package com.skillbox.blogengine.controllers;

import com.skillbox.blogengine.dto.RestorePasswordData;
import com.skillbox.blogengine.service.EmailSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiAuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    EmailSender emailSender;

    @Test
    void testSending() throws Exception {
        RestorePasswordData passwordData = new RestorePasswordData("moder@mail.ru");
        String passwordDataJson = mapper.writeValueAsString(passwordData);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/restore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordDataJson)
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();


    }
}
