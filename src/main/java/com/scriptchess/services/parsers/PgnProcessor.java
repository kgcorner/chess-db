package com.scriptchess.services.parsers;

import com.scriptchess.models.Game;
import com.scriptchess.models.Player;
import com.scriptchess.models.Tournament;
import com.scriptchess.util.PGNDateParser;
import com.scriptchess.util.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 15/09/22
 */

public interface PgnProcessor {
    Logger PGN_PROCESSOR_LOGGER = LogManager.getLogger(PgnProcessor.class);
    Game parsePgn(String fullPgn);
    boolean supports(String fullpgn);

    default Game parsePgn(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    default List<Game> parseMultiGamePgn(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    default Game fillGameMetadata(Map<String, String> gameMetadata, Game game) {
        if(gameMetadata == null || gameMetadata.size() == 0)
            return game;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Player whitePlayer = new Player();
        Player blackPlayer = new Player();
        Tournament tournament = new Tournament();
        for(Map.Entry<String, String> entry : gameMetadata.entrySet()) {
            String key = entry.getKey();
            switch (key.toLowerCase()) {
                case "event":
                    game.setEvent(entry.getValue());
                    break;
                case "site":
                    game.setSite(entry.getValue());
                    break;
                case "date":
                    game.setDate(PGNDateParser.parseDate(entry.getValue()));
                    break;
                case "round":
                    game.setRound(entry.getValue());
                    break;
                case "white":
                    whitePlayer.setName(entry.getValue());
                    break;
                case "black":
                    blackPlayer.setName(entry.getValue());
                    break;
                case "result":
                    game.setResult(entry.getValue());
                    if(entry.getValue().trim().equals("1-0")) {
                        game.setWhiteWinner(true);
                    } else {
                        if(entry.getValue().trim().equals("1/2-1/2") || entry.getValue().trim().equals("1/2")) {
                            game.setDraw(true);
                        }
                    }
                    break;
                case "blackelo":
                    try {
                        blackPlayer.setElo(Double.parseDouble(entry.getValue()));
                    } catch (NumberFormatException x) {
                        PGN_PROCESSOR_LOGGER.error(x.getMessage());
                    }
                    break;
                case "blackfideid":
                    blackPlayer.setFideId(entry.getValue());
                    break;
                case "tournament":
                    tournament.setName(entry.getValue());
                    break;
                case "whiteelo":
                    try {
                        whitePlayer.setElo(Double.parseDouble(entry.getValue()));
                    } catch (NumberFormatException x) {
                        PGN_PROCESSOR_LOGGER.error(x.getMessage());
                    }
                    break;
                case "whitefideid":
                    whitePlayer.setFideId(entry.getValue());
                    break;
                case "eco":
                    game.setEco(entry.getValue());
                    break;
                default:
                    game.getOtherDetails().put(key, entry.getValue());
            }
        }
        if(tournament != null && Strings.isNullOrEmpty(tournament.getName())) {
            tournament.setName(game.getEvent());
        }
        game.setTournament(tournament);
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        if(game.getDate() != null && game.getTournament() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(game.getDate());
            tournament.setYear(c.get(Calendar.YEAR));
        }
        return game;
    }
}