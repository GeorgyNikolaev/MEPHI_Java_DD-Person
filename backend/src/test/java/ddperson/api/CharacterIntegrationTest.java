package ddperson.api;

import ddperson.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CharacterIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private jakarta.servlet.http.Cookie[] authCookies;

    @BeforeEach
    void login() throws Exception {
        String email = "char-" + System.nanoTime() + "@example.com";
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"password123","displayName":"Игрок"}
                        """.formatted(email)));

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"password123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();

        authCookies = login.getResponse().getCookies();
    }

    @Test
    void createAndListCharacter_persistsRowAndReturnsViaSelect() throws Exception {
        mockMvc.perform(post("/api/v1/characters")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Аэlarion",
                                  "description": "Эльфийский следопыт",
                                  "roleArchetype": "RANGER",
                                  "universeStyle": "FORGOTTEN_REALMS",
                                  "seriousnessLevel": 7,
                                  "expressivenessLevel": 6,
                                  "mood": "BROODING"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Аэlarion"))
                .andExpect(jsonPath("$.roleArchetype.labelRu").value("Следопыт"));

        mockMvc.perform(get("/api/v1/characters")
                        .cookie(authCookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Аэlarion"));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM characters WHERE name = ?",
                Integer.class,
                "Аэlarion"
        );
        assertThat(count).isEqualTo(1);
    }
}
