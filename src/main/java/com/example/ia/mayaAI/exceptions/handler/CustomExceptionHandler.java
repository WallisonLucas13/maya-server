package com.example.ia.mayaAI.exceptions.handler;

import com.example.ia.mayaAI.exceptions.AlreadyUserRegisteredException;
import com.example.ia.mayaAI.exceptions.InvalidCredentialsException;
import com.example.ia.mayaAI.exceptions.NotFoundUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AlreadyUserRegisteredException.class)
    public ResponseEntity<String> handleUserAlreadyRegisteredException(
            AlreadyUserRegisteredException e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(
            InvalidCredentialsException e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({NotFoundUserException.class, UsernameNotFoundException.class})
    public ResponseEntity<String> handleNotFoundUserException(
            Exception e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
