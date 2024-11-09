package com.example.ia.mayaAI.exceptions;

public class AlreadyUserRegisteredException extends RuntimeException {

    public AlreadyUserRegisteredException(String message) {
        super(message);
    }
}
