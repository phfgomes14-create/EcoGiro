package com.example.ecogiro.service;
import com.example.ecogiro.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PlanCompatibilityServiceTest {
    final PlanCompatibilityService service=new PlanCompatibilityService();
    @Test void todasAsCombinacoesDeCategoriaETipo(){
        for(VehicleType type:VehicleType.values()) {
            assertTrue(service.accepts("TODOS",type));
            assertEquals(type!=VehicleType.BIKE,service.accepts("MOTO_E_ELETRICOS",type));
            for(VehicleType category:VehicleType.values())assertEquals(type==category,service.accepts(category.name(),type));
            assertFalse(service.accepts("DESCONHECIDA",type));
        }
    }
    @Test void planoInativoOuVeiculoIndisponivelNaoPodeSerContratado(){
        RentalPlan plan=new RentalPlan();plan.setVehicleCategory("MOTO");
        Vehicle vehicle=new Vehicle();vehicle.setType(VehicleType.MOTO);vehicle.setStatus(VehicleStatus.AVAILABLE);
        assertDoesNotThrow(()->service.requireCompatible(plan,vehicle));
        vehicle.setType(VehicleType.BIKE);assertThrows(IllegalArgumentException.class,()->service.requireCompatible(plan,vehicle));
        vehicle.setType(VehicleType.MOTO);
        for(VehicleStatus status:new VehicleStatus[]{VehicleStatus.RENTED,VehicleStatus.MAINTENANCE,VehicleStatus.INACTIVE}){
            vehicle.setStatus(status);assertThrows(IllegalStateException.class,()->service.requireCompatible(plan,vehicle));
        }
        vehicle.setStatus(VehicleStatus.AVAILABLE);plan.setActive(false);assertThrows(IllegalStateException.class,()->service.requireCompatible(plan,vehicle));
    }
    @Test void categoriaNulaOuInvalidaFalhaFechada(){assertFalse(service.accepts(null,VehicleType.MOTO));assertFalse(service.accepts("TODOS",null));assertThrows(IllegalArgumentException.class,()->service.requireKnownCategory("outro"));}
}
