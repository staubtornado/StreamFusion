package de.streamfusion.exceptions;

public class NoValidEmailException extends Exception{
    public NoValidEmailException(String message) {
        super(message);
    }
}
