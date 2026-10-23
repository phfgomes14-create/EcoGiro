package com.example.ecogiro.service;
import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.junit.jupiter.api.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class PlanRequestServiceTest {
    final PlanRequestRepository requests=mock(PlanRequestRepository.class);
    final RentalPlanRepository plans=mock(RentalPlanRepository.class);
    final VehicleRepository vehicles=mock(VehicleRepository.class);
    final UserPlanSubscriptionRepository subscriptions=mock(UserPlanSubscriptionRepository.class);
    final PlanRequestService service=new PlanRequestService(requests,plans,subscriptions,vehicles,new PlanCompatibilityService());
    RentalPlan plan;Vehicle vehicle;AppUser user;
    @BeforeEach void setup(){plan=new RentalPlan();plan.setVehicleCategory("MOTO");vehicle=new Vehicle();vehicle.setType(VehicleType.MOTO);vehicle.setStatus(VehicleStatus.AVAILABLE);user=new AppUser();when(plans.findById(1L)).thenReturn(Optional.of(plan));when(vehicles.findById(2L)).thenReturn(Optional.of(vehicle));when(requests.save(any())).thenAnswer(a->a.getArgument(0));}
    @Test void solicitacaoExigeVeiculoEValidaCompatibilidade(){
        assertThrows(IllegalArgumentException.class,()->service.requestPlan(user,1L,null,"teste"));
        vehicle.setType(VehicleType.BIKE);assertThrows(IllegalArgumentException.class,()->service.requestPlan(user,1L,2L,"teste"));verify(requests,never()).save(any());
    }
    @Test void solicitacaoCompativelPersisteVinculo(){assertSame(vehicle,service.requestPlan(user,1L,2L,"teste").getVehicle());}
    @Test void aprovacaoRevalidaCategoriaAlterada(){var request=new PlanRequest();request.setPlan(plan);request.setVehicle(vehicle);when(requests.lockById(3L)).thenReturn(Optional.of(request));plan.setVehicleCategory("BIKE");assertThrows(IllegalArgumentException.class,()->service.respond(3L,PlanRequestStatus.APPROVED,"ok",new AppUser()));verifyNoInteractions(subscriptions);}
    @Test void decisaoDuplicadaNaoCriaAssinatura(){var request=new PlanRequest();request.setStatus(PlanRequestStatus.APPROVED);when(requests.lockById(3L)).thenReturn(Optional.of(request));assertThrows(IllegalStateException.class,()->service.respond(3L,PlanRequestStatus.APPROVED,"ok",new AppUser()));verifyNoInteractions(subscriptions);}
}
