package com.syncdroids.fileengine.exception;

public class ServerUninitializedException extends Exception {
    public ServerUninitializedException() {
        super("Server IP and/or port not set.");
    }
}
