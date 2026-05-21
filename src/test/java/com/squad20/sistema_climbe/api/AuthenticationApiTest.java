package com.squad20.sistema_climbe.api;

import com.squad20.sistema_climbe.domain.security.repository.RefreshTokenRepository;
import com.squad20.sistema_climbe.domain.user.entity.Role;
import com.squad20.sistema_climbe.domain.user.entity.User;
import com.squad20.sistema_climbe.domain.user.repository.UserRepository;
import com.squad20.sistema_climbe.domain.security.entity.RefreshToken;
import com.squad20.sistema_climbe.domain.security.service.RefreshTokenService;
import com.squad20.sistema_climbe.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationApiTest {

    private static final java.util.concurrent.atomic.AtomicLong AUTH_SEED =
            new java.util.concurrent.atomic.AtomicLong(500_000_001L);

    private static long nextAuthSeed() {
        return AUTH_SEED.getAndIncrement();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private User setupUser(String email, String password, String status) {
        long seed = nextAuthSeed();
        return userRepository.save(User.builder()
                .fullName("Auth Teste")
                .email(email)
                .cpf(generateValidCpf(seed))
                .phone("11999999999")
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.ANALISTA)
                .status(status)
                .build());
    }

    @Test
    @DisplayName("Login com credenciais válidas devolve cookies de autenticação")
    void loginReturnsCookiesForValidCredentials() throws Exception {
        String email = "login" + nextAuthSeed() + "@teste.com";
        String password = "Senha123";
        setupUser(email, password, "ATIVO");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        assertCookiePresent(result, "accessToken");
        assertCookiePresent(result, "refreshToken");
    }

    @Test
    @DisplayName("Refresh com cookie válido devolve novo cookie de access token")
    void refreshReturnsNewAccessTokenCookie() throws Exception {
        String email = "refresh" + nextAuthSeed() + "@teste.com";
        User u = setupUser(email, "Senha123", "ATIVO");
        RefreshToken rt = refreshTokenService.createRefreshToken(u.getId());

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", rt.getToken())))
                .andExpect(status().isOk())
                .andReturn();

        assertCookiePresent(refreshResult, "accessToken");
    }

    @Test
    @DisplayName("Logout limpa os cookies de autenticação")
    void logoutClearsCookies() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andReturn();

        assertCookieCleared(result, "accessToken");
        assertCookieCleared(result, "refreshToken");
    }

    @Test
    @DisplayName("GET /api/auth/me sem autenticação retorna 401")
    void getMeSemAutenticacaoRetorna401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/auth/me com usuário autenticado retorna dados do usuário")
    void getMeComUsuarioAutenticadoRetornaDados() throws Exception {
        String email = "me." + nextAuthSeed() + "@teste.com";
        setupUser(email, "Senha123", "ATIVO");

        mockMvc.perform(get("/api/auth/me").with(user(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @DisplayName("GET /api/auth/me com usuário inexistente no banco retorna 401")
    void getMeComUsuarioInexistenteRetorna401() throws Exception {
        String email = "fantasma." + nextAuthSeed() + "@teste.com";

        mockMvc.perform(get("/api/auth/me").with(user(email)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/request-access para usuário PENDENTE retorna 200")
    void requestAccessParaUsuarioPendenteRetorna200() throws Exception {
        String email = "req.acc." + nextAuthSeed() + "@teste.com";
        setupUser(email, "Senha123", "PENDENTE");

        mockMvc.perform(post("/api/auth/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/request-access para email inexistente retorna 404")
    void requestAccessParaEmailInexistenteRetorna404() throws Exception {
        String email = "ninguem." + nextAuthSeed() + "@teste.com";

        mockMvc.perform(post("/api/auth/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/auth/request-access para usuário já ATIVO retorna 400")
    void requestAccessParaUsuarioAtivoRetorna400() throws Exception {
        String email = "ativo.req." + nextAuthSeed() + "@teste.com";
        setupUser(email, "Senha123", "ATIVO");

        mockMvc.perform(post("/api/auth/request-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isBadRequest());
    }

    private static String extractCookie(MvcResult result, String cookieName) {
        Cookie cookie = result.getResponse().getCookie(cookieName);
        if (cookie == null) {
            throw new AssertionError("Cookie não encontrado: " + cookieName);
        }
        return cookie.getValue();
    }

    private static void assertCookiePresent(MvcResult result, String cookieName) {
        Cookie cookie = result.getResponse().getCookie(cookieName);
        if (cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            throw new AssertionError("Cookie ausente ou vazio: " + cookieName);
        }
    }

    private static void assertCookieCleared(MvcResult result, String cookieName) {
        Cookie cookie = result.getResponse().getCookie(cookieName);
        if (cookie == null) {
            throw new AssertionError("Cookie não encontrado: " + cookieName);
        }
        if (cookie.getMaxAge() != 0) {
            throw new AssertionError("Cookie não foi limpo: " + cookieName);
        }
    }

    private static String generateValidCpf(long seed) {
        int[] digits = new int[11];
        long value = seed;

        for (int i = 0; i < 9; i++) {
            digits[i] = (int) (value % 10);
            value /= 10;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int remainder = sum % 11;
        digits[9] = remainder < 2 ? 0 : 11 - remainder;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        remainder = sum % 11;
        digits[10] = remainder < 2 ? 0 : 11 - remainder;

        StringBuilder cpf = new StringBuilder(11);
        for (int digit : digits) {
            cpf.append(digit);
        }
        return cpf.toString();
    }
}
