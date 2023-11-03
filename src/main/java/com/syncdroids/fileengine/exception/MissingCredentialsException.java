package com.syncdroids.fileengine.exception;

public class MissingCredentialsException extends Exception {
    public MissingCredentialsException() {
        super("Username and/or password not set.");
    }

}
