package com.scriptchess.services.parsers;


import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.util.FileUtil;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 21/09/22
 */

public class GenericPgnProcessor implements PgnProcessor {
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
        allMovesStr = allMovesStr.replaceAll(Pattern.quote("."), ". ") //replace all "." with ". "
            .replaceAll(Pattern.quote(". . . "),"... ") //replace all ". . . " (due to above replace with "... "
            .replaceAll(Pattern.quote("{")," { ")
            .replaceAll(Pattern.quote("}")," } ")
            .replaceAll(Pattern.quote("(")," ( ")
            .replaceAll(Pattern.quote(")")," ) ")
            .replaceAll("  "," ");
        String[] tokens = allMovesStr.split(" ");
        TOKEN_TYPE currentToken = null;
        int commentType1Count = 0; //for all open (
        int commentType2Count = 0; //for all open {
        String REGEX_CLOCK = "\\{ \\[%clk [0-9]{2}:[0-9]{2}:[0-9]{2}\\] \\}";
        String REGEX_MOVE = "^[RNBKQa-hO][a-h1-8x+#=O\\-]{1,5}$";
        String MOVE_COUNT_REGEX = "^[0-9]+[\\.]{1,3}$";
        Pattern clockPattern = Pattern.compile(REGEX_CLOCK);
        Pattern movePattern = Pattern.compile(REGEX_MOVE);
        Pattern moveCountPattern = Pattern.compile(MOVE_COUNT_REGEX);
        List<Move> moves = new ArrayList<>();
        StringBuilder commentBuilder = new StringBuilder();
        Move move = null;
        int currentMoveNum = 0;
        for(String token : tokens) {
            if(token.startsWith("(") || token.startsWith("{"))
                currentToken = TOKEN_TYPE.COMMENT;

            if(currentToken == TOKEN_TYPE.COMMENT) {
                commentBuilder.append(token+" ");
                if(token.length() == 1) {
                    if(token.equals("(")) {
                        currentToken = TOKEN_TYPE.COMMENT;
                        commentType1Count++;
                        continue;
                    }
                    if(token.equals("{")) {
                        currentToken = TOKEN_TYPE.COMMENT;
                        commentType2Count++;
                        continue;
                    }

                    if(token.equals(")")) {
                        currentToken = TOKEN_TYPE.COMMENT;
                        commentType1Count--;
                        if(commentType1Count == 0 && commentType2Count == 0) {
                            currentToken = null;
                            move.setComment(commentBuilder.toString());
                        }
                        continue;
                    }
                    if(token.equals("}")) {
                        currentToken = TOKEN_TYPE.COMMENT;
                        commentType2Count--;
                        if(commentType1Count == 0 && commentType2Count == 0) {
                            currentToken = null;
                            move.setComment(commentBuilder.toString());
                        }
                        continue;
                    }
                }
            }
            else {
                if(clockPattern.matcher(token).find()) {
                    move.setMove(token);
                    continue;
                }
                if(moveCountPattern.matcher(token).find()) {
                    currentToken = TOKEN_TYPE.MOVE_COUNT;
                    move = new Move();
                    token = token.replaceAll(Pattern.quote("."),"");
                    currentMoveNum = Integer.parseInt(token);
                    move.setMoveNumber(currentMoveNum);
                    continue;
                }
                if(movePattern.matcher(token).find()) {
                    currentToken = TOKEN_TYPE.MOVE;
                    if(moves.size() == 0 || moves.size() % 2 == 0) {
                        //It's white's move
                        move.setMove(token);
                        moves.add(move);
                    } else {
                        move = new Move();
                        move.setMove(token);
                        move.setMoveNumber(currentMoveNum);
                        moves.add(move);
                    }


                }
            }
        }

        game.setMoves(moves);

        return game;
    }

    @Override
    public boolean supports(String fullpgn) {
        return true;
    }
}

enum TOKEN_TYPE {
    MOVE,
    COMMENT,
    MOVE_COUNT
}