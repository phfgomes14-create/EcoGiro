package com.example.ecogiro;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:security;DB_CLOSE_DELAY=-1","spring.datasource.username=sa","spring.datasource.password="})
class SessionSecurityTest {
    @Autowired WebApplicationContext context;
    MockMvc mvc;
    @BeforeEach void setup(){mvc=MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();}
    @Test void logoutInvalidaSessaoEExigeNovoLogin() throws Exception {
        var session=new MockHttpSession();
        var auth=UsernamePasswordAuthenticationToken.authenticated("fixture",null,List.of(new SimpleGrantedAuthority("ROLE_ADMIN_GERAL")));
        session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth));
        mvc.perform(get("/api/session/profile").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.role").value("ADMIN_GERAL"));
        mvc.perform(post("/logout").session(session)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login.html?logout=1"));
        assertTrue(session.isInvalid());
        mvc.perform(get("/api/session/profile")).andExpect(status().is3xxRedirection());
    }
    @Test void usuarioNaoAcessaOperacaoAdministrativa() throws Exception {
        mvc.perform(get("/api/admin/operations/stores").with(user("fixture").roles("USER"))).andExpect(status().isForbidden());
        mvc.perform(get("/operacao.html").with(user("fixture").roles("USER"))).andExpect(status().isForbidden());
    }
    @Test void setorialNaoAcessaPainelGlobalNemAprovaPropostas() throws Exception {
        mvc.perform(get("/admin.html").with(user("fixture").roles("ADMIN","ADMIN_SETORIAL"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/redistribution-proposals/pending").with(user("fixture").roles("ADMIN","ADMIN_SETORIAL"))).andExpect(status().isForbidden());
    }
    @Test void novaEscritaExigeCsrf() throws Exception {
        mvc.perform(post("/api/admin/operations/sectors").with(user("fixture").roles("ADMIN_GERAL"))
            .contentType("application/json").content("{\"code\":\"X\",\"name\":\"Centro\"}")).andExpect(status().isForbidden());
    }
    @Autowired com.example.ecogiro.repository.AppUserRepository users;
    @Test @org.springframework.transaction.annotation.Transactional
    void setorDuplicadoRetornaConflitoComMensagemClara() throws Exception {
        var actor=new com.example.ecogiro.model.AppUser();
        actor.setFullName("Administrador de teste"); actor.setCpf("98765432101");actor.setAge(25);
        actor.setEmail("duplicate@ecogiro.test");actor.setPassword("fixture");actor.setRole(com.example.ecogiro.model.Role.ADMIN_GERAL);
        users.saveAndFlush(actor);
        String body="{\"code\":\"TEST-DUPLICATE\",\"name\":\"Setor de teste\"}";
        mvc.perform(post("/api/admin/operations/sectors").with(user(actor.getEmail()).roles("ADMIN_GERAL"))
            .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf().asHeader())
            .contentType("application/json").content(body)).andExpect(status().isOk());
        mvc.perform(post("/api/admin/operations/sectors").with(user(actor.getEmail()).roles("ADMIN_GERAL"))
            .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf().asHeader())
            .contentType("application/json").content(body)).andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("Já existe um setor com esse código. Selecione o setor existente ou use outro código."));
    }

}
