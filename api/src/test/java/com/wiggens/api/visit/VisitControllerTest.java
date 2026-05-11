package com.wiggens.api.visit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiggens.api.WiggensApiApplication;
import com.wiggens.api.auth.dto.RegisterRequest;
import com.wiggens.api.patient.dto.PatientProfileRequest;
import com.wiggens.api.visit.dto.VisitCompleteRequest;
import com.wiggens.api.visit.dto.VisitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = WiggensApiApplication.class)
@AutoConfigureMockMvc
class VisitControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    String email = "visit@example.com";
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
    void addListAndCompleteVisit() throws Exception {
        VisitRequest vr = new VisitRequest();
        vr.setAt(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        vr.setProviderRole("Nurse");
        vr.setNotes("Routine check");
        var addRes = mvc.perform(post("/codex-example/api/v1/visits/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(vr)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerRole", is("Nurse")))
                .andReturn();
        long id = mapper.readTree(addRes.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(get("/codex-example/api/v1/visits/me")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        VisitCompleteRequest cr = new VisitCompleteRequest();
        cr.setVisitNotes("Vitals normal");
        cr.setVitals("BP 120/80");
        cr.setCareChanges("None");

        mvc.perform(put("/codex-example/api/v1/visits/"+id+"/complete")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cr)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        mvc.perform(get("/codex-example/api/v1/visits/me/past")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk());
    }
}

