package com.secretlab.kvstore.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
