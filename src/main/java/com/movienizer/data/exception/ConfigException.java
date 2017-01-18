package com.movienizer.data.exception;

public class ConfigException extends Exception {

    private static final long serialVersionUID = -3800553404833923805L;

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable exception) {
        super(message, exception);
    }
}