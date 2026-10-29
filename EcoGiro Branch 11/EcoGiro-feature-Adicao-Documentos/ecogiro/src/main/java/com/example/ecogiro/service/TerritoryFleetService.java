package com.example.ecogiro.service;
import com.example.ecogiro.model.*;
import com.example.ecogiro.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TerritoryFleetService {
    private final TerritoryAccessService access;
    private final VehicleRepository vehicles;
    private final EcoGiroStoreRepository stores;
    public TerritoryFleetService(TerritoryAccessService access, VehicleRepository vehicles, EcoGiroStoreRepository stores) {
        this.access=access; this.vehicles=vehicles; this.stores=stores;
    }
    @Transactional
    public void move(Authentication authentication, Long vehicleId, Long storeId) {
        AppUser actor=access.actor(authentication);
        // A loja de destino serializa a verificação de capacidade entre transferências concorrentes.
        EcoGiroStore target=stores.lockById(storeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        access.require(actor,target);
        Vehicle vehicle=vehicles.lockById(vehicleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        access.require(actor,vehicle.getStore());
        if (vehicle.getStatus()==VehicleStatus.RENTED) throw new ResponseStatusException(HttpStatus.CONFLICT,"Veículo alugado não pode ser transferido.");
        if (!target.isActive() || !target.getSector().isActive() || target.isBlocked())
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Destino inativo ou bloqueado pela administração.");
        boolean same=vehicle.getStore()!=null && vehicle.getStore().getId().equals(target.getId());
        if (!same && vehicles.countByStoreId(storeId)>=target.getCapacity())
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Loja de destino sem capacidade.");
        vehicle.setStore(target); vehicle.setLatitude(target.getLatitude()); vehicle.setLongitude(target.getLongitude());
        vehicle.setLocationName(target.getName()); vehicles.save(vehicle);
    }
    @Transactional
    public void status(Authentication authentication, Long vehicleId, VehicleStatus status) {
        AppUser actor=access.actor(authentication);
        Vehicle vehicle=vehicles.lockById(vehicleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        access.require(actor,vehicle.getStore());
        if (status==null || status==VehicleStatus.RENTED || vehicle.getStatus()==VehicleStatus.RENTED)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Aluguel deve ser tratado pelo fluxo de contratação.");
        vehicle.setStatus(status); vehicles.save(vehicle);
    }
}
