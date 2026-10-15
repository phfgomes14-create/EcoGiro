package com.example.ecogiro.config;

import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.RentalPlanRepository;
import com.example.ecogiro.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedEcoGiro(RentalPlanRepository planRepository, VehicleRepository vehicleRepository) {
        return args -> {
            if (planRepository.count() == 0) {
                planRepository.save(plan("Flex Semanal", "Para testar a EcoGiro ou atender uma necessidade temporária.", "TODOS", "79.90", 7));
                planRepository.save(plan("Urbano Mensal", "Plano para deslocamentos cotidianos com acesso à frota urbana.", "TODOS", "199.90", 30));
                planRepository.save(plan("Entregador Mensal", "Uso profissional com foco em produtividade e maior frequência de locação.", "MOTO_E_ELETRICOS", "449.90", 30));
                planRepository.save(plan("Corporativo", "Plano mensal para empresas e equipes com mobilidade recorrente.", "TODOS", "699.90", 30));
            }

            if (vehicleRepository.count() == 0) {
                vehicleRepository.save(vehicle("ECO-M001", "Moto EcoGiro", VehicleType.MOTO, -15.793889, -47.882778, "Plano Piloto"));
                vehicleRepository.save(vehicle("ECO-E001", "E-bike Urbana", VehicleType.E_BIKE, -15.839100, -48.028100, "Águas Claras"));
                vehicleRepository.save(vehicle("ECO-B001", "Bike Urbana", VehicleType.BIKE, -15.833500, -48.057400, "Taguatinga"));
                vehicleRepository.save(vehicle("ECO-S001", "Patinete Elétrico", VehicleType.SCOOTER, -15.817100, -48.108100, "Ceilândia"));
                vehicleRepository.save(vehicle("ECO-M002", "Moto EcoGiro", VehicleType.MOTO, -16.015100, -48.062200, "Gama"));
                vehicleRepository.save(vehicle("ECO-S002", "Patinete Elétrico", VehicleType.SCOOTER, -15.650500, -47.792000, "Sobradinho"));
            }
        };
    }

    private RentalPlan plan(String name, String description, String category, String price, int days) {
        RentalPlan plan = new RentalPlan();
        plan.setName(name);
        plan.setDescription(description);
        plan.setVehicleCategory(category);
        plan.setPrice(new BigDecimal(price));
        plan.setDurationDays(days);
        plan.setActive(true);
        return plan;
    }

    private Vehicle vehicle(String code, String model, VehicleType type, double lat, double lng, String location) {
        Vehicle vehicle = new Vehicle();
        vehicle.setCode(code);
        vehicle.setModel(model);
        vehicle.setType(type);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setLatitude(lat);
        vehicle.setLongitude(lng);
        vehicle.setLocationName(location);
        return vehicle;
    }
}
