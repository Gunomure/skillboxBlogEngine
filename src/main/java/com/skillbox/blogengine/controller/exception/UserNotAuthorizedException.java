package com.skillbox.blogengine.controller.exception;

public class UserNotAuthorizedException extends RuntimeException {

    public UserNotAuthorizedException(String message) {
        super(message);
    }
}
