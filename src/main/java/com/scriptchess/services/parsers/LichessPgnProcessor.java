package com.scriptchess.services.parsers;


import com.scriptchess.models.Game;
import com.scriptchess.models.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 16/09/22
 */

public class LichessPgnProcessor implements PgnProcessor {
    @Override
    public Game parsePgn(String fullPgn) {
        Game game = new Game();
        String moveRegex = "[0-9]+[/.] ";
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
                    allMovesStr+=line;
                }
            }
        }
        game = fillGameMetadata(details, game);
        allMovesStr =allMovesStr.replaceAll(Pattern.quote("."),". ").replaceAll(Pattern.quote(". . . "),"...").replaceAll("  "," ");
        String[] allMoves = allMovesStr.split(moveRegex);
        int moveNum = 1;
        List<Move> moveList = new ArrayList<Move>();
        for(String move : allMoves) {
            if(move.equals("")) {
                continue;
            }
            if(move.contains("...") && !move.contains("... ")) {
                move = move.replace("...", "... ");
            }
            String blackSeqRegex = "[0-9]+[/.]+ ";
            String[] moves = move.split(blackSeqRegex);
            for (String m : moves) {
                String moveWithTimeRegEx = "([A-Za-z0-9\\-\\+=]+).*\\{ \\[%clk ([0-9]{1}:[0-9]{2}:[0-9]{2})] }.*";
                Pattern pattern = Pattern.compile(moveWithTimeRegEx);
                Matcher matcher = pattern.matcher(m);
                if(matcher.find()) {
                    Move moveObj = new Move();
                    moveObj.setMove(matcher.group(1));
                    moveObj.setMoveNumber(moveNum);
                    moveList.add(moveObj);
                }
            }
            moveNum++;
        }
        game.setMoves(moveList);
        return game;
    }

    @Override
    public boolean supports(String fullPgn) {
        return matchSite(fullPgn, "lichess*");
    }
}