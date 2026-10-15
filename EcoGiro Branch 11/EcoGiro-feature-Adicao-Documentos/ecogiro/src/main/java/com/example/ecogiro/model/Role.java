package com.example.ecogiro.model;

public enum Role {
    USER,
    ADMIN, // Legado: mantido para nao revogar acesso de contas existentes.
    ADMIN_GERAL,
    ADMIN_SETORIAL;

    public boolean isAdministrator() {
        return this == ADMIN || this == ADMIN_GERAL || this == ADMIN_SETORIAL;
    }
}
