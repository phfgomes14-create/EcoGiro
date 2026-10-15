package com.example.ecogiro.dto;
import jakarta.validation.constraints.*;
/** O protocolo é gerado no servidor e não integra este contrato. */
public record PlanRequestInput(@NotNull Long planId, @NotNull Long vehicleId, @Size(max=500) String message) {}
