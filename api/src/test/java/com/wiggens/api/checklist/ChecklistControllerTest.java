package com.wiggens.api.checklist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiggens.api.WiggensApiApplication;
import com.wiggens.api.auth.dto.RegisterRequest;
import com.wiggens.api.checklist.dto.CareTaskRequest;
import com.wiggens.api.checklist.dto.CompleteTaskRequest;
import com.wiggens.api.patient.dto.PatientProfileRequest;
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
class ChecklistControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    String email = "checklist@example.com";
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
    void createListCompleteAndHistory() throws Exception {
        CareTaskRequest t = new CareTaskRequest();
        t.setName("Oral Care");
        t.setFrequencyType(FrequencyType.TIMES_PER_DAY);
        t.setTimesPerDay(2);
        t.setNotes("Brush gently");

        var created = mvc.perform(post("/codex-example/api/v1/checklist/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(t)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();
        long id = mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(get("/codex-example/api/v1/checklist/me").header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Oral Care")));

        CompleteTaskRequest done = new CompleteTaskRequest();
        done.setAt(Instant.now());
        mvc.perform(post("/codex-example/api/v1/checklist/"+id+"/complete")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(done)))
                .andExpect(status().isNoContent());

        mvc.perform(get("/codex-example/api/v1/checklist/history").header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskName", is("Oral Care")));
    }
}

