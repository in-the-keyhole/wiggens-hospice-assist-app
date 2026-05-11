package com.wiggens.api.medication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiggens.api.WiggensApiApplication;
import com.wiggens.api.auth.dto.RegisterRequest;
import com.wiggens.api.medication.dto.MedicationLogRequest;
import com.wiggens.api.medication.dto.MedicationRequest;
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
class MedicationControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    String email = "meds@example.com";
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
    void addListArchiveAndLogPrn() throws Exception {
        var m1 = new MedicationRequest();
        m1.setName("Morphine");
        m1.setStrength("5mg");
        m1.setScheduleType(MedicationScheduleType.SCHEDULED);
        m1.setScheduleTimes("08:00,20:00");

        var m2 = new MedicationRequest();
        m2.setName("Acetaminophen");
        m2.setStrength("500mg");
        m2.setScheduleType(MedicationScheduleType.SCHEDULED);
        m2.setScheduleTimes("12:00");

        mvc.perform(post("/codex-example/api/v1/medications/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(m1)))
                .andExpect(status().isOk());
        mvc.perform(post("/codex-example/api/v1/medications/me")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(m2)))
                .andExpect(status().isOk());

        mvc.perform(get("/codex-example/api/v1/medications/me")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].scheduleTimes", containsString("08:00")))
                .andExpect(jsonPath("$[1].scheduleTimes", containsString("12:00")));

        // PRN requires reason when logging
        var prn = new MedicationRequest();
        prn.setName("Oxycodone");
        prn.setScheduleType(MedicationScheduleType.PRN);
        var prnId = mapper.readTree(
                mvc.perform(post("/codex-example/api/v1/medications/me")
                                .header("Authorization", "Bearer "+token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(prn)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        ).get("id").asLong();

        MedicationLogRequest log = new MedicationLogRequest();
        log.setAt(Instant.now());
        // Missing reason should fail
        mvc.perform(post("/codex-example/api/v1/medications/"+prnId+"/logs")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(log)))
                .andExpect(status().is4xxClientError());

        log.setReason("Pain level 7");
        mvc.perform(post("/codex-example/api/v1/medications/"+prnId+"/logs")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(log)))
                .andExpect(status().isNoContent());
    }
}

