package com.scriptchess.services;


import com.scriptchess.models.*;
import com.scriptchess.services.parsers.ByteWisePGNProcessor;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.PGNDateParser;
import com.scriptchess.util.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 22/10/22
 */

public class PgnCompressorDecompressor {
    private static final Logger LOGGER = LogManager.getLogger(PgnCompressorDecompressor.class);
    public static final String GAME_EXTENSION = ".game";

    /**
     * Reads the game file
     * @param path path of the game file without extension
     * @return read games
     */
    public static List<Game> readGames(String path) {
        List<Game> games=new ArrayList<>();
        try {
            int position = 0;
            path = path +GAME_EXTENSION;
            if(!FileUtil.fileExists(path))
                return null;
            byte[] bytes = FileUtil.readBytes(path);
            List<byte[]> gamesBytesList = splitByteArray(bytes, TinyMove.GAME_SEPARATOR);

            for(byte[] gameBytes : gamesBytesList) {
                List<byte[]> detailsAndMovesList = splitByteArray(gameBytes, TinyMove.DETAIL_MOVE_SEPARATOR);
                byte[] detailsBytesArr = detailsAndMovesList.get(0);
                byte[] movesBytesArr = detailsAndMovesList.get(1);
                List<byte[]> detailsByteList = splitByteArray(detailsBytesArr, TinyMove.SEPARATOR);
                List<byte[]> movesByteList = splitByteArray(movesBytesArr, TinyMove.SEPARATOR);
                Game game = new Game();
                //Set details
                game.setEvent(new String(detailsByteList.get(0)));
                game.setSite(new String(detailsByteList.get(1)));
                Date date = PGNDateParser.parseDate(new String(detailsByteList.get(2)));
                game.setDate(date);
                game.setRound(new String(detailsByteList.get(3)));
                Player whitePlayer = new Player();
                Player blackPlayer = new Player();
                game.setWhitePlayer(whitePlayer);
                game.setBlackPlayer(blackPlayer);
                whitePlayer.setName(new String(detailsByteList.get(4)));
                try {
                    whitePlayer.setElo(Double.parseDouble(new String(detailsByteList.get(5))));
                } catch (NumberFormatException x) {
                    LOGGER.error(x.getMessage(),x);
                }
                blackPlayer.setName(new String(detailsByteList.get(6)));
                try {
                    blackPlayer.setElo(Double.parseDouble(new String(detailsByteList.get(7))));
                } catch (NumberFormatException x) {
                    LOGGER.error(x.getMessage(),x);
                }
                game.setResult(new String(detailsByteList.get(8)));
                game.setEco(new String(detailsByteList.get(9)));
                Tournament t = new Tournament();
                t.setName(new String(detailsByteList.get(10)));
                game.setTournament(t);
                if(detailsByteList.size() > 11) {
                    Map<String, String> otherDetails = new HashMap<>();
                    for (int i = 11; i < detailsByteList.size(); i++) {
                        String detail = new String(detailsByteList.get(i));
                        String[] detailArr = detail.split(" ");
                        otherDetails.put(detailArr[0], detailArr[1]);
                    }
                    game.setOtherDetails(otherDetails);
                }
                //set moves
                List<Move> moves = new ArrayList<>();
                int counter = 0;
                boolean whitesMove = true;
                for(byte[] moveBytes : movesByteList) {
                    TinyMove tinyMove = TinyMove.getTinyMoveFromNativeMove(moveBytes);
                    String moveStr = TinyMove.getMove(tinyMove);
                    Move move = new Move();
                    move.setMove(moveStr);
                    moves.add(move);
                    if(whitesMove) {
                        counter++;
                    }
                    whitesMove = !whitesMove;
                    move.setMoveNumber(counter);
                }
                switch (game.getResult()) {
                    case "1-0":
                        game.setWhiteWinner(true);
                        game.setDraw(false);
                        break;
                    case"1/2":
                    case"1/2-1/2":
                        game.setWhiteWinner(false);
                        game.setDraw(true);
                        break;
                }
                game.setMoves(moves);
                games.add(game);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
        return games;
    }

    private static List<byte[]> splitByteArray(byte[]bytes, byte separator) {
        List<byte[]> list = new ArrayList<>();
        List<Byte> tmpList = new ArrayList<>();
        for(byte b : bytes) {
            if(b != separator) {
                tmpList.add(b);
            } else {
                list.add(getBytes(tmpList));
                tmpList = new ArrayList<>();
            }
        }
        if(tmpList.size() > 0)
            list.add(getBytes(tmpList));
        return list;
    }

    private static byte[] getBytesForValOrEmpty(String value) {
        return Strings.isNullOrEmpty(value) ? "".getBytes() : value.getBytes();
    }

    /**
     * Writes given game into given file
     * @param game game to write
     * @param path Path of the file where game will be written. Don't add extension
     */
    public static void writeGame(Game game, String path) {
        List<Move> moves = game.getMoves();
        //List<TinyMove> tinyMoves = new ArrayList<>();

        List<Byte> bytes = new ArrayList<>();

        for(byte b: game.getEvent().getBytes()) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        for(byte b: getBytesForValOrEmpty(game.getSite())) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        String date = PGNDateParser.formatDate(game.getDate());
        for(byte b: date.getBytes()) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);

        for(byte b: getBytesForValOrEmpty(game.getRound())) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        for(byte b: getBytesForValOrEmpty(game.getWhitePlayer().getName())) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);

        for(byte b: getBytesForValOrEmpty(game.getWhitePlayer().getElo()+"")) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        for(byte b: getBytesForValOrEmpty(game.getBlackPlayer().getName())) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        for(byte b: getBytesForValOrEmpty(game.getBlackPlayer().getElo() + "")) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        for(byte b: getBytesForValOrEmpty(game.getResult())) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        for(byte b: getBytesForValOrEmpty(game.getEco())) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        String tournament = game.getTournament() == null ? "" : game.getTournament().getName();
        for(byte b: tournament.getBytes()) {
            bytes.add(b);
        }
        bytes.add(TinyMove.SEPARATOR);
        if(game.getOtherDetails() != null && game.getOtherDetails().size() > 0) {
            for (Map.Entry<String, String> entry : game.getOtherDetails().entrySet()) {
                String format = entry.getKey() + " " + entry.getValue();
                for(byte b: format.getBytes()) {
                    bytes.add(b);
                }
                bytes.add(TinyMove.SEPARATOR);
            }

        }
        bytes.add(TinyMove.DETAIL_MOVE_SEPARATOR);
        for(Move move : moves) {
            TinyMove tinyMove = TinyMove.getTinyMove(move.getMove());
            byte[] tinyMoveBytes = TinyMove.getNativeMove(tinyMove);
            for(byte b: tinyMoveBytes) {
                bytes.add(b);
            }
            bytes.add(TinyMove.SEPARATOR);
        }
        byte[] byteArr = getBytes(bytes);
        bytes.add(TinyMove.GAME_SEPARATOR);
        File file = new File(path);
        File dir = new File(file.getParent());
        if(!dir.exists()) {
            dir.mkdirs();
        }
        FileUtil.writeData(byteArr, true, path + GAME_EXTENSION);
    }

    private static byte[] getBytes(List<Byte> bytes) {
        byte[] byteArr = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArr[i] = bytes.get(i);
        }
        return byteArr;
    }
}