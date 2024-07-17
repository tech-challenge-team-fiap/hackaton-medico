package com.fiap.hackathon.medico.domain.exception;

import lombok.Getter;

@Getter
public abstract class InvalidProcessException extends Exception {

    @Getter
    private final String tittle;
    private final String message;

    public InvalidProcessException(String tittle, String message) {
        this.tittle = tittle;
        this.message = message;
    }

}
