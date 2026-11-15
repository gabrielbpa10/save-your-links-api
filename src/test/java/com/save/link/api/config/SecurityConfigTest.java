package com.save.link.api.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityConfigTest {
    private AnnotationConfigWebApplicationContext context;
    private MockMvc mvc;

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, Pages.class})
    static class Config {}

    @RestController
    static class Pages {
        @GetMapping({"/login", "/signup", "/signup-valida-email", "/signup-valida-password", "/css/style.css", "/meus-links"})
        String page() { return "ok"; }
    }

    @BeforeEach
    void setup() {
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(Config.class);
        context.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean(FilterChainProxy.class)).build();
    }

    @AfterEach
    void close() { context.close(); }

    @Test
    void signupAndAssetsAreAccessibleWithoutLogin() throws Exception {
        for (String path : new String[]{"/login", "/signup", "/signup-valida-email", "/signup-valida-password", "/css/style.css"}) {
            mvc.perform(get(path)).andExpect(status().isOk());
        }
    }

    @Test
    void signupIsAccessibleWithApplicationContextPath() throws Exception {
        mvc.perform(get("/save-your-links/signup").contextPath("/save-your-links"))
                .andExpect(status().isOk());
    }

    @Test
    void applicationPagesStillRequireLogin() throws Exception {
        mvc.perform(get("/meus-links")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void signupPostsStillRequireCsrfProtection() throws Exception {
        mvc.perform(post("/cadastrar-novo-usuario")).andExpect(status().isForbidden());
    }
}
