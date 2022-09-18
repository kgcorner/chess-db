package com.scriptchess.exceptions;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 17/09/22
 */

public class DAOException extends Exception {
    public DAOException() {
        super("Problem occurred in DAO Operation");
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(Exception x) {
        super(x);
    }
}