package com.wiggens.api.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiggens.api.WiggensApiApplication;
import com.wiggens.api.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = WiggensApiApplication.class)
@AutoConfigureMockMvc
class UploadControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    String email = "upload@example.com";
    String password = "Password123!";
    String token;

    @BeforeEach
    void setup() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword(password);
        mvc.perform(post("/codex-example/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        var res = mvc.perform(post("/codex-example/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();
        var tokenJson = res.getResponse().getContentAsString();
        this.token = mapper.readTree(tokenJson).get("token").asText();
    }

    @Test
    void uploadAndDownload() throws Exception {
        byte[] content = "hello-image".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "hello.png", "image/png", content);
        var uploadRes = mvc.perform(multipart("/codex-example/api/v1/uploads").file(file)
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.url", containsString("/codex-example/api/v1/uploads/")))
                .andReturn();
        var url = mapper.readTree(uploadRes.getResponse().getContentAsString()).get("url").asText();

        mvc.perform(get(url).header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().bytes(content))
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(header().string("Content-Type", containsString("image/png")));
    }
}

