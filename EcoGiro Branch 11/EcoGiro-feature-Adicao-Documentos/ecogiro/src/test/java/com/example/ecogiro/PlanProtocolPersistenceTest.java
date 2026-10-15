package com.example.ecogiro;
import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:protocol;DB_CLOSE_DELAY=-1","spring.datasource.username=sa","spring.datasource.password="})
@Transactional
class PlanProtocolPersistenceTest {
 @Autowired AppUserRepository users;@Autowired RentalPlanRepository plans;@Autowired PlanRequestRepository requests;
 @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
 @Test void protocoloGeradoNoServidorEImutavelNasDecisoes(){
  AppUser user=new AppUser();user.setFullName("Teste protocolo");user.setEmail("protocol@ecogiro.test");user.setCpf("12345678909");user.setAge(23);user.setPassword("fixture");users.saveAndFlush(user);
  RentalPlan plan=new RentalPlan();plan.setName("Teste protocolo");plan.setDescription("Teste");plan.setPrice(BigDecimal.ONE);plan.setDurationDays(7);plan.setVehicleCategory("TODOS");plans.saveAndFlush(plan);
  PlanRequest first=new PlanRequest();first.setUser(user);first.setPlan(plan);ReflectionTestUtils.setField(first,"protocol","enviado-pelo-cliente");requests.saveAndFlush(first);
  String protocol=first.getProtocol();assertEquals(protocol,UUID.fromString(protocol).toString());assertNotEquals("enviado-pelo-cliente",protocol);
  PlanRequest second=new PlanRequest();second.setUser(user);second.setPlan(plan);requests.saveAndFlush(second);assertNotEquals(protocol,second.getProtocol());
  first.setStatus(PlanRequestStatus.CANCELLED);requests.saveAndFlush(first);assertEquals(protocol,first.getProtocol());
  assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
    ()->jdbc.update("update tb_solicitacao_plano set protocol=? where id=?",protocol,second.getId()));
 }
 @Test void legadoPossuiReferenciaEstavelSemAlterarRegistro(){var request=new PlanRequest();ReflectionTestUtils.setField(request,"id",42L);assertEquals("LEGACY-42",request.getProtocol());}
}
