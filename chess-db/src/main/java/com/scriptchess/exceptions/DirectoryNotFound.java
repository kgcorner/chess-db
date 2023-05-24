package com.scriptchess.exceptions;


/**
 * Description : Exception to be thrown when directory is not found
 * Author: kumar
 * Created on : 16/09/22
 */

public class DirectoryNotFound extends Exception {
    public DirectoryNotFound() {
        super("No such directory exists");
    }

    public DirectoryNotFound(String message) {
        super(message);
    }
}