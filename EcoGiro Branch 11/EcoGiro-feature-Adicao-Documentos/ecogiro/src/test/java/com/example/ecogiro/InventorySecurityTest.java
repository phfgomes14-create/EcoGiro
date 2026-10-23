package com.example.ecogiro;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:inventory-security;DB_CLOSE_DELAY=-1","spring.datasource.username=sa","spring.datasource.password="})
class InventorySecurityTest {
    @Autowired WebApplicationContext context;
    MockMvc mvc;
    @BeforeEach void setup(){mvc=MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();}
    @Test void estoqueExigeLoginEBloqueiaUsuarioComum() throws Exception {
        mvc.perform(get("/estoque.html")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/estoque.html").with(user("fixture").roles("USER"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/operations/inventory").with(user("fixture").roles("USER"))).andExpect(status().isForbidden());
    }
    @Test void administradoresAcessamPaginaSemLiberarPainelGlobalAoSetorial() throws Exception {
        for(String role: new String[]{"ADMIN_GERAL","ADMIN_SETORIAL","ADMIN_LEGACY"})
            mvc.perform(get("/estoque.html").with(user("fixture").roles(role))).andExpect(status().isOk());
        mvc.perform(get("/api/admin/vehicles").with(user("fixture").roles("ADMIN_SETORIAL"))).andExpect(status().isForbidden());
    }
}
