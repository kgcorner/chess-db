package com.scriptchess.exceptions;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 20/09/22
 */

public class ChessDbException extends Exception {
    public ChessDbException() {
        super("Unknown error occurred");
    }

    public ChessDbException(String message) {
        super(message);
    }

    public ChessDbException(Exception x) {
        super(x);
    }
}