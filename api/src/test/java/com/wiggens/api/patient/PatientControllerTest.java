package com.wiggens.api.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiggens.api.WiggensApiApplication;
import com.wiggens.api.auth.dto.RegisterRequest;
import com.wiggens.api.patient.dto.PatientProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = WiggensApiApplication.class)
@AutoConfigureMockMvc
class PatientControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    String email = "care@example.com";
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
    void createAndUpdateProfile() throws Exception {
        PatientProfileRequest create = new PatientProfileRequest();
        create.setFullName("John Doe");
        create.setContactEmail("john@example.com");

        mvc.perform(post("/codex-example/api/v1/patients")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("John Doe")));

        mvc.perform(get("/codex-example/api/v1/patients/me")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactEmail", is("john@example.com")));

        PatientProfileRequest update = new PatientProfileRequest();
        update.setFullName("John A. Doe");
        update.setContactEmail("johnny@example.com");

        mvc.perform(put("/codex-example/api/v1/patients/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("John A. Doe")))
                .andExpect(jsonPath("$.contactEmail", is("johnny@example.com")));
    }
}

