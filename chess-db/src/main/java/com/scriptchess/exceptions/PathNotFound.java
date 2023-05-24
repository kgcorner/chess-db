package com.scriptchess.exceptions;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 16/09/22
 */

public class PathNotFound extends Exception {
    public PathNotFound() {
        super("No such path exists");
    }

    public PathNotFound(String message) {
        super(message);
    }
}