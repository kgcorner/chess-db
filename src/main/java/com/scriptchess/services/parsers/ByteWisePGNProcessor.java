package com.scriptchess.services.parsers;


import com.google.common.primitives.Bytes;
import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 23/09/22
 */

public class ByteWisePGNProcessor implements PgnProcessor {

    private static final Map<Byte, String> commentTokenMap = new HashMap<>();
    private static final Map<Byte, String> detailsTokenMap = new HashMap<>();
    private static final Map<Byte, String> movesTokenMap = new HashMap<>();
    private static final Map<Byte, String> sequenceMap = new HashMap<>();
    private static final Map<Byte, String> ignoreCharMap = new HashMap<>();
    public static final int QUOTES = 34;
    private static byte NEW_LINE = 10;
    private static byte SPACE = 32;
    private static byte OPEN_DETAIL_BRACKET = 91;
    private static final Logger LOGGER = LogManager.getLogger(ByteWisePGNProcessor.class);
    static
    {
        String[] commentTokens = {"(", ")", "{", "}", "(", ")"};
        String[] movesToken = {"a","b","c","d","e","f","g","h","R","B","K","Q","N","x","=",
            "+", "#", "O", "-",
            "1","2","3","4","5","6","7","8"};
        String[] detailsToken = {"[","]"};
        //prepares the symbols
        String[] numbersToken = {".","0","1","2","3","4","5","6","7","8","9"};
        String[] ignoreToken = {"$"};
        for(String token : commentTokens) {
            commentTokenMap.put(token.getBytes()[0], token);
        }

        for(String token : detailsToken) {
            detailsTokenMap.put(token.getBytes()[0], token);
        }

        for(String token : movesToken) {
            movesTokenMap.put(token.getBytes()[0], token);
        }

        for(String token : numbersToken) {
            sequenceMap.put(token.getBytes()[0], token);
        }

        for(String token : ignoreToken) {
            ignoreCharMap.put(token.getBytes()[0], token);
        }
    }
    @Override
    public Game parsePgn(String fullPgn) {
        List<Game> games = parseMultiGamePgn((fullPgn + "\n").getBytes());
        if(games != null && games.size() > 0) {
            return games.get(0);
        }
        return null;
    }

    @Override
    public List<Game> parseMultiGamePgn(byte[] bytes) {
        List<Game> games = new ArrayList<>();
        List<Byte> detailsBytes = new ArrayList<>();
        List<Byte> movesSeqBytes = new ArrayList<>();
        List<Byte> moveBytes = new ArrayList<>();
        List<Byte> commentBytes = new ArrayList<>();
        List<String> details = new ArrayList<>();
        TOKEN_TYPE currentTokenType = null;
        int comment1TokenCount = 0; //for (
        int comment2TokenCount = 0; //for {
        int blankLineCount = 0;
        boolean wasPreviousCharNewLine = false;
        List<Byte> pgnByteList = new ArrayList<>();
        List<Move> moves = new ArrayList<>();
        int currentMoveCount = 0;
        Move currentMove = null;
        int counter = 0;
        long starts = new Date().getTime();
        for (int i = 0; i < bytes.length; i++) {
            counter = i;
            byte b = bytes[i];
            if(b == OPEN_DETAIL_BRACKET && moves.size() > 0 && currentTokenType == null) {
                //This is starting of new Game so save the previous one
                createAndAddGame(games, details, moves, pgnByteList);
                pgnByteList = new ArrayList<>();
                moves = new ArrayList<>();
                long ends = new Date().getTime();
                long timeTaken = (ends - starts);
                //LOGGER.debug("Time taken in game " + games.size()+" = " + timeTaken +"ms");
                starts = ends;
                moveBytes = new ArrayList<>();
                detailsBytes = new ArrayList<>();
                movesSeqBytes = new ArrayList<>();
                commentBytes = new ArrayList<>();
                currentTokenType = null;
            }
            pgnByteList.add(b);
            if(currentTokenType == null || currentTokenType == TOKEN_TYPE.SKIP) {
                if(ignoreCharMap.containsKey(b)) {
                    currentTokenType = TOKEN_TYPE.SKIP;
                    continue;
                } else {
                    if(currentTokenType == TOKEN_TYPE.SKIP) {
                        if(b == SPACE || b == NEW_LINE) {
                            currentTokenType = null;
                            continue;
                        } else {
                            if(!commentTokenMap.containsKey(b)) {
                                continue;
                            } else {
                                currentTokenType = TOKEN_TYPE.COMMENT;
                            }
                        }
                    }
                }
            }


            /**
             * Creates Details
             */
            if(currentTokenType == null || currentTokenType == TOKEN_TYPE.DETAIL) {
                if(detailsTokenMap.containsKey(b)) {
                    String s = detailsTokenMap.get(b);
                    if(s.equals("[")) {
                        currentTokenType = TOKEN_TYPE.DETAIL;
                    }
                    if(s.equals("]")) {
                        currentTokenType = null;
                        details.add(getString(detailsBytes));
                        detailsBytes = new ArrayList<>();
                        continue;
                    }
                } else {
                    if(currentTokenType == TOKEN_TYPE.DETAIL) {
                        if(b != QUOTES)
                            detailsBytes.add(b);
                    }
                }
            }
            /**
             * Creates Details ends
             */

            /**
             * Creates Comment
             */
            if(currentTokenType == null || currentTokenType == TOKEN_TYPE.COMMENT) {
                if(commentTokenMap.containsKey(b)) {
                    commentBytes.add(b);
                    String s = commentTokenMap.get(b);
                    if(s.equals("{")) {
                        currentTokenType = TOKEN_TYPE.COMMENT;
                        comment2TokenCount++;
                        continue;
                    }

                    if(s.equals("(")) {
                        currentTokenType = TOKEN_TYPE.COMMENT;
                        comment1TokenCount++;
                        continue;
                    }

                    if(s.equals(")")) {
                        currentTokenType = TOKEN_TYPE.COMMENT;
                        comment1TokenCount--;
                    }

                    if(s.equals("}")) {
                        currentTokenType = TOKEN_TYPE.COMMENT;
                        comment2TokenCount--;
                    }
                    if(comment1TokenCount == 0 && comment2TokenCount == 0) {

                        if(currentMove != null) {
                            String comment= getString(commentBytes);
                            if(comment.contains("{ [")) {
                                String moveWithTimeRegEx = "\\{ \\[%clk ([0-9]{2}:[0-9]{2}:[0-9]{2})] }.*";
                                Pattern pattern = Pattern.compile(moveWithTimeRegEx);
                                Matcher matcher = pattern.matcher(comment);
                                if (matcher.find() && Strings.isNullOrEmpty(currentMove.getComment())) {
                                    currentMove.setMoveTime(comment.replace("{ [%clk ","").replace("] }",""));
                                } else {
                                    currentMove.setComment(currentMove.getComment() + comment);
                                }
                            } else {
                                currentMove.setComment(currentMove.getComment() + comment);
                            }

                        }
                        commentBytes = new ArrayList<>();
                        currentTokenType = null;
                        continue;
                    }
                } else {
                    if(currentTokenType == TOKEN_TYPE.COMMENT) {
                        commentBytes.add(b);
                        continue;
                    }
                }
            }
            /**
             * Creates Comment ends
             */

            /**
             * Creates move sequence
             */
            if(currentTokenType == null || currentTokenType == TOKEN_TYPE.MOVE_COUNT) {
                if(sequenceMap.containsKey(b)) {
                    currentTokenType = TOKEN_TYPE.MOVE_COUNT;
                    if(sequenceMap.get(b).equals(".")) {
                        if(movesSeqBytes.size() > 0)
                            currentMoveCount = Integer.parseInt(getString(movesSeqBytes));
                        currentTokenType = null;
                        movesSeqBytes = new ArrayList<>();
                        continue;
                    } else {
                        movesSeqBytes.add(b);
                        continue;
                    }
                }else {
                    if(currentTokenType == TOKEN_TYPE.MOVE_COUNT)
                        currentTokenType = null;
                }
            }
            /**
             * Creates move sequence end
             */

            /**
             * Creates move sequence
             */
            if(currentTokenType == null || currentTokenType == TOKEN_TYPE.MOVE) {
                if(movesTokenMap.containsKey(b)) {
                    if(b == 45 && currentTokenType == null) {
                        continue;
                    }
                    currentTokenType = TOKEN_TYPE.MOVE;
                    moveBytes.add(b);
                } else {
                    if(currentTokenType == TOKEN_TYPE.MOVE && (b == SPACE || !movesTokenMap.containsKey(b))) {
                        if(moves.size() %2 == 0) {
                            currentMove = new Move();
                            currentMove.setMove(getString(moveBytes));
                            currentMove.setMoveNumber(currentMoveCount);
                        } else {
                            currentMove = new Move();
                            currentMove.setMoveNumber(currentMoveCount);
                            currentMove.setMove(getString(moveBytes));
                        }
                        moves.add(currentMove);
                        currentTokenType = null;
                        moveBytes = new ArrayList<>();
                        //a move can terminate just before comment starts
                        if(b != SPACE && commentTokenMap.containsKey(b)) {
                            commentBytes.add(b);
                            String s = commentTokenMap.get(b);
                            if(s.equals("{")) {
                                currentTokenType = TOKEN_TYPE.COMMENT;
                                comment2TokenCount++;
                                continue;
                            }

                            if(s.equals("(")) {
                                currentTokenType = TOKEN_TYPE.COMMENT;
                                comment1TokenCount++;
                                continue;
                            }
                        }
                        continue;
                    }
                }
            }
            /**
             * Creates move sequence ends
             */
        }
        LOGGER.debug("Total Byte read :" + counter);
        LOGGER.debug("Total Byte needed to read :" + bytes.length);
        createAndAddGame(games, details, moves, pgnByteList);

        return games;
    }

    private void createAndAddGame(List<Game> games, List<String> details, List<Move> moves, List<Byte> pgn) {
        if(moves.size() > 0 && moves.get(0).getMoveNumber() == 1) {
            Game game = new Game();
            game.setMoves(moves);
            games.add(game);
            Map<String, String> detailMap = new HashMap<>();
            for (String detail : details) {
                String[] dArr = detail.split(" ");
                String value = "";
                if (dArr.length > 2) {
                    for (int i = 1; i < dArr.length; i++) {
                        value += dArr[i] + " ";
                    }
                } else {
                    if(dArr.length > 1)
                        value = dArr[1];
                }
                detailMap.put(dArr[0], value.trim());
            }
            fillGameMetadata(detailMap, game);
            game.setPgn(getString(pgn));
            game.setMd5(Strings.getMd5(game.exportInPgn()));
            details = new ArrayList<>();
        }
    }

    private String getString(List<Byte> bytes) {
        return new String(Bytes.toArray(bytes));
    }

    @Override
    public boolean supports(String fullpgn) {
        return true;
    }

    enum TOKEN_TYPE {
        MOVE,
        COMMENT,
        SKIP,
        MOVE_COUNT,
        DETAIL
    }
}

