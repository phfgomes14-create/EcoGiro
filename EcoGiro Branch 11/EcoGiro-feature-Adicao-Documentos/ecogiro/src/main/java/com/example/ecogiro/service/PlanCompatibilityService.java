package com.example.ecogiro.service;
import com.example.ecogiro.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

/** Regras explícitas para as categorias usadas pelo cadastro de planos. */
@Service
public class PlanCompatibilityService {
    public boolean accepts(String category, VehicleType type) {
        if(category==null || type==null) return false;
        return switch(category.trim().toUpperCase(Locale.ROOT)) {
            case "TODOS" -> true;
            case "MOTO_E_ELETRICOS" -> type==VehicleType.MOTO || type==VehicleType.E_BIKE || type==VehicleType.SCOOTER;
            case "MOTO" -> type==VehicleType.MOTO;
            case "BIKE" -> type==VehicleType.BIKE;
            case "E_BIKE" -> type==VehicleType.E_BIKE;
            case "SCOOTER" -> type==VehicleType.SCOOTER;
            default -> false;
        };
    }
    public void requireCompatible(RentalPlan plan, Vehicle vehicle) {
        if(!plan.isActive()) throw new IllegalStateException("Plano indisponível.");
        if(vehicle==null || !accepts(plan.getVehicleCategory(),vehicle.getType()))
            throw new IllegalArgumentException("O modelo do veículo não é compatível com este plano.");
        if(vehicle.getStatus()!=VehicleStatus.AVAILABLE)
            throw new IllegalStateException("Veículo indisponível para esta solicitação.");
    }
    public void requireKnownCategory(String category) {
        if(Arrays.stream(VehicleType.values()).noneMatch(type -> accepts(category,type)))
            throw new IllegalArgumentException("Categoria inválida. Use TODOS, MOTO_E_ELETRICOS, MOTO, BIKE, E_BIKE ou SCOOTER.");
    }
}
