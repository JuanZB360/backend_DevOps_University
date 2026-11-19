package com.devopsuniversity.player.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * Test de integración de la API completa: HTTP → casos de uso → JPA → H2 (con migraciones Flyway).
 * Recorre lo que hace el frontend: registrarse, guardar progreso, cerrar sesión y volver a entrar.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class PlayerApiIntegrationTest {

    private static final String PASSWORD = "clave-segura-1";

    @Autowired
    private MockMvcTester mvc;

    private MvcTestResult post(String uri, String json) {
        return mvc.post().uri(uri).contentType(MediaType.APPLICATION_JSON).content(json).exchange();
    }

    private static String credentials(String username, String password) {
        return "{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password);
    }

    private static String body(MvcTestResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    /** Registra una cuenta y devuelve su token de sesión. */
    private String register(String username) throws UnsupportedEncodingException {
        MvcTestResult result = post("/api/v1/auth/register", credentials(username, PASSWORD));
        assertThat(result).hasStatus(HttpStatus.CREATED);
        return JsonPath.read(body(result), "$.token");
    }

    private MvcTestResult saveProgress(String token, String json) {
        return mvc.put().uri("/api/v1/me/progress")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();
    }

    private MvcTestResult me(String token) {
        return mvc.get().uri("/api/v1/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token).exchange();
    }

    @Test
    void fullAccountLifecycle() throws Exception {
        String token = register("trinity");

        assertThat(me(token)).hasStatusOk().bodyJson().extractingPath("$.username").isEqualTo("trinity");

        // Guarda progreso con la partida completa incrustada como JSON
        assertThat(saveProgress(token, "{\"xp\":250,\"level\":3,\"points\":40,\"completedMissions\":6,\"saveSnapshot\":{\"version\":1,\"xp\":250}}"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.progress.saveSnapshot.xp").isEqualTo(250);

        // Un progreso más antiguo se rechaza con 409...
        assertThat(saveProgress(token, "{\"xp\":10,\"level\":1,\"points\":0,\"completedMissions\":0}"))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.code").isEqualTo("STALE_PROGRESS");

        // Cerrar sesión invalida el token al instante
        assertThat(mvc.post().uri("/api/v1/auth/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .hasStatus(HttpStatus.NO_CONTENT);
        assertThat(me(token)).hasStatus(HttpStatus.UNAUTHORIZED).bodyJson().extractingPath("$.code").isEqualTo("UNAUTHENTICATED");

        // Volver a entrar devuelve la partida guardada (desde la base de datos)
        MvcTestResult login = post("/api/v1/auth/login", credentials("Trinity ", PASSWORD));
        assertThat(login).hasStatusOk().bodyJson().extractingPath("$.player.progress.saveSnapshot.xp").isEqualTo(250);

        // ...salvo que se pida reemplazar (reiniciar la partida)
        String newToken = JsonPath.read(body(login), "$.token");
        assertThat(saveProgress(newToken, "{\"xp\":0,\"level\":1,\"points\":20,\"completedMissions\":0,\"replace\":true}"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.progress.xp").isEqualTo(0);
    }

    @Test
    void rejectsWrongPasswordAndUnknownUserWithTheSameError() {
        post("/api/v1/auth/register", credentials("morpheus", PASSWORD));

        assertThat(post("/api/v1/auth/login", credentials("morpheus", "otra-clave-9")))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson().extractingPath("$.code").isEqualTo("INVALID_CREDENTIALS");
        assertThat(post("/api/v1/auth/login", credentials("nadie", PASSWORD)))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson().extractingPath("$.code").isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    void validatesRegistration() throws Exception {
        register("oracle");

        assertThat(post("/api/v1/auth/register", credentials("oracle", PASSWORD)))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson().extractingPath("$.code").isEqualTo("USERNAME_TAKEN");
        assertThat(post("/api/v1/auth/register", credentials("1abc", PASSWORD)))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.code").isEqualTo("INVALID_CREDENTIALS_FORMAT");
        assertThat(post("/api/v1/auth/register", credentials("cypher", "corta1")))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.code").isEqualTo("INVALID_CREDENTIALS_FORMAT");
        assertThat(post("/api/v1/auth/register", "{\"username\":\"\",\"password\":\"\"}"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.code").isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void protectedEndpointsRequireASession() {
        assertThat(mvc.get().uri("/api/v1/me")).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(me("token-inventado")).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(mvc.put().uri("/api/v1/me/progress").contentType(MediaType.APPLICATION_JSON)
                .content("{\"xp\":1,\"level\":1,\"points\":0,\"completedMissions\":0}"))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void leaderboardOrdersByXp() throws Exception {
        saveProgress(register("rankbajo"), "{\"xp\":5,\"level\":1,\"points\":0,\"completedMissions\":0}");
        saveProgress(register("rankalto"), "{\"xp\":9000,\"level\":9,\"points\":0,\"completedMissions\":0}");

        assertThat(mvc.get().uri("/api/v1/leaderboard?limit=1"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].username").isEqualTo("rankalto");
    }

    @Test
    void healthEndpointIsUp() {
        assertThat(mvc.get().uri("/actuator/health")).hasStatusOk();
    }
}
