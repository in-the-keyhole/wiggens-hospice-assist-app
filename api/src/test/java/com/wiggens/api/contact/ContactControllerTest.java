package com.wiggens.api.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiggens.api.WiggensApiApplication;
import com.wiggens.api.auth.dto.RegisterRequest;
import com.wiggens.api.contact.dto.ContactRequest;
import com.wiggens.api.patient.dto.PatientProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = WiggensApiApplication.class)
@AutoConfigureMockMvc
class ContactControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    String email = "contacter@example.com";
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

        PatientProfileRequest profile = new PatientProfileRequest();
        profile.setFullName("Pat One");
        mvc.perform(post("/codex-example/api/v1/patients")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(profile)))
                .andExpect(status().isOk());
    }

    @Test
    void addAndListContacts() throws Exception {
        ContactRequest c = new ContactRequest();
        c.setName("Nurse Joy");
        c.setRole(ContactRole.NURSE);
        c.setPhone("555-123-4567");

        mvc.perform(post("/codex-example/api/v1/contacts/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(c)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nurse Joy")))
                .andExpect(jsonPath("$.role", is("NURSE")));

        mvc.perform(get("/codex-example/api/v1/contacts/me")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].phone", is("555-123-4567")));
    }
}

