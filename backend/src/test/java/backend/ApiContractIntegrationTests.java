package backend;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiContractIntegrationTests {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @Sql("/test-word.sql")
    void documentedApiFlowWorksEndToEnd() throws Exception {
        var register = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username":"student01","nickname":"小词同学","email":"student@example.com","password":"123456"}
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.user.settings.dailyNewWords").value(20))
                .andReturn();

        JsonNode registered = objectMapper.readTree(register.getResponse().getContentAsString());
        String accessToken = registered.path("data").path("accessToken").asText();
        Cookie refreshCookie = register.getResponse().getCookie("wordharbor_refresh");

        mockMvc.perform(get("/users/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("student01"));

        mockMvc.perform(put("/users/me/settings").header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"dailyNewWords":25,"dailyReviewLimit":100,"preferredLevel":"CET6","reminderTime":"21:15","soundEnabled":false}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredLevel").value("CET6"));

        mockMvc.perform(get("/words").header("Authorization", "Bearer " + accessToken)
                        .param("level", "CET6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].word").value("contemplate"));

        mockMvc.perform(get("/words/101").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.senses[0].definitionCn").value("深思，仔细考虑"));

        var queue = mockMvc.perform(get("/study/queue").header("Authorization", "Bearer " + accessToken)
                        .param("mode", "NEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andReturn();
        String sessionId = objectMapper.readTree(queue.getResponse().getContentAsString())
                .path("data").path("sessionId").asText();

        mockMvc.perform(post("/study/answers").header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"sessionId":"%s","wordId":101,"rating":"GOOD","responseTimeMs":4200}
                            """.formatted(sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.intervalDays").value(4))
                .andExpect(jsonPath("$.data.mastery").value("FAMILIAR"));

        mockMvc.perform(get("/study/dashboard").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.learnedToday").value(1));

        mockMvc.perform(get("/statistics/overview").header("Authorization", "Bearer " + accessToken)
                        .param("range", "7D"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalLearned").value(1));

        var refreshed = mockMvc.perform(post("/auth/refresh").cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.expiresIn").value(7200))
                .andReturn();
        Cookie rotatedCookie = refreshed.getResponse().getCookie("wordharbor_refresh");

        mockMvc.perform(post("/auth/logout").header("Authorization", "Bearer " + accessToken).cookie(rotatedCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
