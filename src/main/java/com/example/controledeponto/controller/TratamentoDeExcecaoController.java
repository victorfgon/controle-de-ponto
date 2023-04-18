package com.example.controledeponto.controller;

import com.example.controledeponto.exception.BadRequestException;
import com.example.controledeponto.exception.ConflictException;
import com.example.controledeponto.exception.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TratamentoDeExcecaoController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String badRequestExceptionHandler(BadRequestException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public String conflictExceptionHandler(ConflictException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public String forbiddenExceptionHandler(ForbiddenException e) {
        return e.getMessage();
    }
}
