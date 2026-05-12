package com.wiggens.api.caregiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiggens.api.WiggensApiApplication;
import com.wiggens.api.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = WiggensApiApplication.class)
@AutoConfigureMockMvc
class CaregiverControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    String owner = "owner2@example.com";
    String pw = "Password123!";
    String token;

    @BeforeEach
    void setup() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail(owner);
        req.setPassword(pw);
        mvc.perform(post("/codex-example/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        var res = mvc.perform(post("/codex-example/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();
        this.token = mapper.readTree(res.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void inviteAndAccept() throws Exception {
        var inviteReq = new CaregiverController.InviteRequest();
        inviteReq.setEmail("helper@example.com");
        inviteReq.setRole(Role.CAREGIVER);
        var created = mvc.perform(post("/codex-example/api/v1/caregivers/invite")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inviteReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();
        String tok = mapper.readTree(created.getResponse().getContentAsString()).get("token").asText();

        var accept = new CaregiverController.AcceptRequest();
        accept.setToken(tok);
        accept.setPassword("Password123!");
        mvc.perform(post("/codex-example/api/v1/caregivers/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accept)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }
}
