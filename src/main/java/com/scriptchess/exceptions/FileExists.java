package com.scriptchess.exceptions;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 16/09/22
 */

public class FileExists extends RuntimeException {
    public FileExists() {
        super("File already exists");
    }

    public FileExists(String message) {
        super(message);
    }
}