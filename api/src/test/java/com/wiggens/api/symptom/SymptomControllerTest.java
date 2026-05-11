package com.wiggens.api.symptom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiggens.api.WiggensApiApplication;
import com.wiggens.api.auth.dto.RegisterRequest;
import com.wiggens.api.patient.dto.PatientProfileRequest;
import com.wiggens.api.symptom.dto.SymptomEntryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = WiggensApiApplication.class)
@AutoConfigureMockMvc
class SymptomControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    String token;

    @BeforeEach
    void setup() throws Exception {
        String email = "symptoms+" + System.currentTimeMillis() + "@example.com";
        String password = "Password123!";
        var reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword(password);
        mvc.perform(post("/codex-example/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reg)))
                .andExpect(status().isOk());
        var res = mvc.perform(post("/codex-example/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reg)))
                .andExpect(status().isOk())
                .andReturn();
        this.token = mapper.readTree(res.getResponse().getContentAsString()).get("token").asText();

        var profile = new PatientProfileRequest();
        profile.setFullName("Pat One");
        mvc.perform(post("/codex-example/api/v1/patients")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(profile)))
                .andExpect(status().isOk());
    }

    @Test
    void addAndListWithFilters() throws Exception {
        var s1 = new SymptomEntryRequest();
        s1.setAt(Instant.parse("2024-01-01T12:00:00Z"));
        s1.setTags(java.util.List.of("pain", "nausea"));
        s1.setPainScore(7);
        s1.setNotes("Felt worse after lunch");

        var s2 = new SymptomEntryRequest();
        s2.setAt(Instant.parse("2024-01-02T08:00:00Z"));
        s2.setTags(java.util.List.of("sleep"));
        s2.setPainScore(3);

        mvc.perform(post("/codex-example/api/v1/symptoms/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(s1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.painScore", is(7)));

        mvc.perform(post("/codex-example/api/v1/symptoms/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(s2)))
                .andExpect(status().isOk());

        // list all
        mvc.perform(get("/codex-example/api/v1/symptoms/me")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // filter by date range
        mvc.perform(get("/codex-example/api/v1/symptoms/me")
                        .header("Authorization", "Bearer "+token)
                        .param("from", "2024-01-02T00:00:00Z")
                        .param("to", "2024-01-03T00:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].painScore", is(3)));

        // filter by tag
        mvc.perform(get("/codex-example/api/v1/symptoms/me")
                        .header("Authorization", "Bearer "+token)
                        .param("tag", "pain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tags", hasItem("pain")));
    }

    @Test
    void validatePainScoreRange() throws Exception {
        var bad = new SymptomEntryRequest();
        bad.setAt(Instant.now());
        bad.setPainScore(99); // invalid
        mvc.perform(post("/codex-example/api/v1/symptoms/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().is4xxClientError());
    }
}
