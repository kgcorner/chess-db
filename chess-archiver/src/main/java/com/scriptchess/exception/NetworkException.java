package com.scriptchess.exception;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

public class NetworkException extends Exception {
    public NetworkException(String unexpected_error_occurred, int i) {
        super(i+":" + unexpected_error_occurred);
    }

    public NetworkException(String s) {
        super(s);
    }
}