package com.scriptchess.resources;


import com.scriptchess.exception.ScriptChessException;
import com.scriptchess.models.MoveDetails;
import com.scriptchess.service.ScriptchessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Map;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */
@RestController
public class MoveResource {
    @Autowired
    private ScriptchessService service;

    @GetMapping("/games/fen")
    public Map<String, MoveDetails> getMovesAndGameCountForFen(@RequestParam("fen") String fen)
        throws ScriptChessException {
        byte[] decode = Base64.getDecoder().decode(fen);
        fen = new String(decode);
        Map<String, MoveDetails> movesAndGameCountForFen = service.getMovesAndGameCountForFen(fen);
        if(movesAndGameCountForFen == null)  {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Move not found in database");
        }
        return movesAndGameCountForFen;
    }
}