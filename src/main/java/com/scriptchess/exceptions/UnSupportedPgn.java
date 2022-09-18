package com.scriptchess.exceptions;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 18/09/22
 */

public class UnSupportedPgn extends Exception {
    public UnSupportedPgn() {
        super("We do not recognize this pgn");
    }

    public UnSupportedPgn(String message) {
        super(message);
    }
}