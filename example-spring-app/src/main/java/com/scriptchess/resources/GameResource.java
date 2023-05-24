package com.scriptchess.resources;


import com.scriptchess.data.model.GameCreationStatus;
import com.scriptchess.data.model.MiniGameModel;
import com.scriptchess.exception.ResourceNotFoundException;
import com.scriptchess.exception.ScriptChessException;
import com.scriptchess.models.Game;
import com.scriptchess.service.ScriptchessService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

@RestController
public class GameResource {

    private static final Logger LOGGER = LogManager.getLogger(GameResource.class);
    @Autowired
    private ScriptchessService service;

    @GetMapping("/games/{gameId}")
    public Game getGame(@PathVariable("gameId") String gameId) {
        return service.getGame(gameId);
    }

    @PostMapping("/games/game-creation-session")
    public String saveGameInBulk(@RequestParam("image") MultipartFile pgnFile) throws IOException {
        LOGGER.info("received request for Creating games, file name: " + pgnFile.getOriginalFilename());
        byte[] bytes = IOUtils.readFully(pgnFile.getInputStream(), (int)pgnFile.getSize());
        try {
            return service.createGameCreationRequestWithPreparedGames(bytes, pgnFile.getOriginalFilename());
        } catch (IllegalStateException x) {
            throw new ResponseStatusException(
                HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, x.getLocalizedMessage(), x);
        }
    }

    @GetMapping("/games/create-requests-status")
    public List<GameCreationStatus> getAllBulkCreateGameRequests() {
        return service.getAllBulkCreateGameRequests();
    }

    @GetMapping("/games/create/{session}")
    public GameCreationStatus getGameCreateSessionStatus(@PathVariable String session)
        throws ScriptChessException, IOException {
        try {
            return service.getGameCreationStatus(session);
        } catch (ResourceNotFoundException x) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, x.getLocalizedMessage(), x);
        }
    }
}