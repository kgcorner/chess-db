package com.scriptchess.services.parsers;


import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 15/09/22
 */

public class ChessComPgnProcessor implements PgnProcessor {

    public static final String CHESS_COM = "chess.com";

    @Override
    public Game parsePgn(String fullPgn) {
        Game game = new Game();
        String moveRegex = "[0-9]+[/.] ";
        //fullPgn = fullPgn.replace("\n","\n ");
        String[] lines = fullPgn.split("\n");
        Map<String, String> details = new HashMap();
        String allMovesStr = "";
        //Parse Details
        for(String line : lines) {
            if(line.startsWith("[") && line.endsWith("]")) {
                line = line.replace("[","").replace("]","").replaceAll("\"","");
                String[] parts = line.split(" ");
                String value = "";
                if(parts.length > 2) {
                    for (int i = 1; i < parts.length; i++) {
                        value += (parts[i] + " ");
                    }
                } else {
                    value = parts[1];
                }

                details.put(parts[0], value.trim());
            } else {
                if(line !="\n") {
                    allMovesStr+=(line +" ");
                }
            }
        }
        game = fillGameMetadata(details, game);
        allMovesStr =allMovesStr.replaceAll(Pattern.quote("."),". ").replaceAll(Pattern.quote(". . . "),"...").replaceAll("  "," ");
        String[] allMoves = allMovesStr.split(moveRegex);
        int moveNum = 1;
        List<Move> moveList = new ArrayList<Move>();
        for(String move : allMoves) {
            if(Strings.isNullOrEmpty(move)) {
                continue;
            }
            String blackSeqRegex = " ";
            String[] moves = move.split(blackSeqRegex);
            for (String m : moves) {
                if(m.equalsIgnoreCase("0-1") || m.equalsIgnoreCase("1-0") || m.equalsIgnoreCase("1/2-1/2")) continue;
                Move moveObj = new Move();
                moveObj.setMove(m);
                moveObj.setMoveNumber(moveNum);
                moveList.add(moveObj);
            }
            moveNum++;
        }
        game.setMoves(moveList);
        return game;
    }

    @Override
    public boolean supports(String fullPgn) {
        return matchSite(fullPgn, CHESS_COM);
    }
}