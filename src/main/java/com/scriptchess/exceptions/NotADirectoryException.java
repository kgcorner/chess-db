package com.scriptchess.exceptions;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 16/09/22
 */

public class NotADirectoryException extends Exception {
    public NotADirectoryException() {
        super("It's not a directory");
    }

    public NotADirectoryException(String message) {
        super(message);
    }
}