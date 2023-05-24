package com.scriptchess.exception;


import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.service.ScriptchessService;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

public class ScriptChessException extends Exception {
    public ScriptChessException() {
        super();
    }

    public ScriptChessException(String message) {
        super(message);
    }

    public ScriptChessException(ChessDbException e) {
        super(e);
    }
}