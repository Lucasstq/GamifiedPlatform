package dev.gamified.GamifiedPlatform.exceptions;

public class AcessDeniedException extends RuntimeException{
    public AcessDeniedException(String message) {
        super(message);
    }
}
