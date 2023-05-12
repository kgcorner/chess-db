package com.scriptchess.models;


import com.scriptchess.util.Strings;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 21/10/22
 */

public class TinyMove {
    public static final String O_O = "O-O";
    public static final String O_O1 = "O-O+";
    public static final String O_O_O = "O-O-O";
    public static final String O_O_O1 = "O-O-O+";
    public static final String O_O2 = "O-O#";
    public static final String O_O_O2 = "O-O-O#";
    private boolean castle;
    private boolean hasStartingPosition;
    private boolean captures;
    private boolean[] startPosition = null; //4 bit
    private boolean[] targetFile = null; // 3bit
    private boolean[] targetRow = null; //3 bit
    private boolean[] operation = null; //3 bit
    private boolean[] piece = null; //3 bit
    private boolean[] promotedPiece = null; //3 bit

    private static final byte ROOK = 1;
    private static final byte KNIGHT = 2;
    private static final byte BISHOP = 3;
    private static final byte QUEEN = 4;
    private static final byte KING = 5;
    private static final byte PAWN = 6;

    private static final byte a = 0;
    private static final byte b = 1;
    private static final byte c = 2;
    private static final byte d = 3;
    private static final byte e = 4;
    private static final byte f = 5;
    private static final byte g = 6;
    private static final byte h = 7;


    private static final byte ONE = 0;
    private static final byte TWO = 1;
    private static final byte THREE = 2;
    private static final byte FOUR = 3;
    private static final byte FIVE = 4;
    private static final byte SIX = 5;
    private static final byte SEVEN = 6;
    private static final byte EIGHT = 7;

    private static final byte CHECK = 1;
    private static final byte MATE = 2;
    private static final byte PROMOTE = 3;
    public static final byte SEPARATOR = Byte.MAX_VALUE;
    public static final byte DETAIL_MOVE_SEPARATOR = Byte.MAX_VALUE - 1;
    public static final byte GAME_SEPARATOR = Byte.MAX_VALUE - 2;

    private static final Map<String, Byte> movePartToNativeEncodingMap = new HashMap<>();
    private static final Map<Byte, String> nativeToMovePartDecodingMap = new HashMap<>();



    private boolean isCastle() {
        return castle;
    }

    private void setCastle(boolean castle) {
        this.castle = castle;
    }

    private boolean isHasStartingPosition() {
        return hasStartingPosition;
    }

    private void setHasStartingPosition(boolean hasStartingPosition) {
        this.hasStartingPosition = hasStartingPosition;
    }

    private boolean isCaptures() {
        return captures;
    }

    private void setCaptures(boolean captures) {
        this.captures = captures;
    }

    private boolean[] getStartPosition() {
        return startPosition;
    }

    private void setStartPosition(boolean[] startPosition) {
        this.startPosition = startPosition;
    }

    private boolean[] getTargetFile() {
        return targetFile;
    }

    private void setTargetFile(boolean[] targetFile) {
        this.targetFile = targetFile;
    }

    private boolean[] getTargetRow() {
        return targetRow;
    }

    private void setTargetRow(boolean[] targetRow) {
        this.targetRow = targetRow;
    }

    private boolean[] getOperation() {
        return operation;
    }

    private void setOperation(boolean[] operation) {
        this.operation = operation;
    }

    private boolean[] getPiece() {
        return piece;
    }

    private void setPiece(boolean[] piece) {
        this.piece = piece;
    }

    public boolean[] getPromotedPiece() {
        return promotedPiece;
    }

    public void setPromotedPiece(boolean[] promotedPiece) {
        this.promotedPiece = promotedPiece;
    }

    private static boolean[] getBooleanArray(byte num, byte maxLen) {
        boolean [] flags = new boolean[maxLen];
        for (int i = maxLen -1; i >= 0; i--) {
            flags[maxLen - 1 - i] = ((num & 0xFF) & (1 << i)) != 0;
        }
        return flags;
    }



    public static TinyMove getTinyMove(String moveStr) {
        String simplePawnMoveRegEx = "^[a-h][1-8][\\+|#]?$";
        String simplePawnCaptureMoveRegEx = "^[a-h][x][a-h][1-8][\\+|#]?$";
        String pawnMoveWithPromotionRegEx = "^[a-h][18][=][RNBQK][\\+|#]?$";
        String pawnCaptureMoveWithPromotionRegEx = "^[a-h][x][a-h][1-8][=][RNBQK][\\+|#]?$";
        String simplePieceMoveRegEx = "^[RNBQK][x]?[a-h][1-8][\\+|#]?$";
        String pieceMoveWithStartingPositionRegEX = "^[RNBQK][a-h1-8][x]?[a-h][1-8][\\+|#]?$";
        String shortCastle = O_O;
        String shortCastleWithCheck = O_O1;
        String longCastle = O_O_O;
        String longCastleWithCheck = O_O_O1;
        String shortCastleWithMate = O_O2;
        String longCastleWithMate = O_O_O2;
        try {
            Pattern pattern = Pattern.compile(simplePawnMoveRegEx);
            Matcher matcher = pattern.matcher(moveStr);
            if (matcher.find()) {
                return getSimplePawnMove(moveStr);
            }
            pattern = Pattern.compile(simplePawnCaptureMoveRegEx);
            matcher = pattern.matcher(moveStr);
            if (matcher.find()) {
                return getSimplePawnCaptureMove(moveStr);
            }
            pattern = Pattern.compile(simplePieceMoveRegEx);
            matcher = pattern.matcher(moveStr);
            if (matcher.find()) {
                return getSimplePieceMove(moveStr, moveStr.contains("x"));
            }
            pattern = Pattern.compile(pieceMoveWithStartingPositionRegEX);
            matcher = pattern.matcher(moveStr);
            if (matcher.find()) {
                return getSimplePieceMoveWithStartingPosition(moveStr, moveStr.contains("x"));
            }

            pattern = Pattern.compile(pawnMoveWithPromotionRegEx);
            matcher = pattern.matcher(moveStr);
            if (matcher.find()) {
                return getPawnMoveWithPromotion(moveStr);
            }

            pattern = Pattern.compile(pawnCaptureMoveWithPromotionRegEx);
            matcher = pattern.matcher(moveStr);
            if (matcher.find()) {
                return getPawnCaptureMoveWithPromotion(moveStr);
            }


            if (moveStr.equalsIgnoreCase(shortCastle)) {
                return new TinyMove();
            }

            if (moveStr.equalsIgnoreCase(longCastle)) {
                TinyMove move = new TinyMove();
                move.setCastle(true);
                return move;
            }

            if (moveStr.equalsIgnoreCase(shortCastleWithCheck)) {
                TinyMove move = new TinyMove();
                move.setOperation(getBooleanArray(CHECK, (byte) 3));
                return move;
            }

            if (moveStr.equalsIgnoreCase(longCastleWithCheck)) {
                TinyMove move = new TinyMove();
                move.setCastle(true);
                move.setOperation(getBooleanArray(CHECK, (byte) 3));
                return move;
            }

            if (moveStr.equalsIgnoreCase(shortCastleWithMate)) {
                TinyMove move = new TinyMove();
                move.setOperation(getBooleanArray(MATE, (byte) 3));
                return move;
            }

            if (moveStr.equalsIgnoreCase(longCastleWithMate)) {
                TinyMove move = new TinyMove();
                move.setCastle(true);
                move.setOperation(getBooleanArray(MATE, (byte) 3));
                return move;
            }
        } catch (IllegalArgumentException x) {
            throw new IllegalArgumentException(x.getMessage()+" move = " + moveStr);
        }
        throw new IllegalArgumentException("Not identifiable move " + moveStr);
    }

    public static String getMove(TinyMove move) {
        StringBuilder sb = new StringBuilder();
        if(move.getPiece() == null) {
            if(move.isCastle()) {
                sb.append("O-O-O");
            } else {
                sb.append("O-O");
            }
            if(move.getOperation() != null) {
                byte val = booleansToByte(move.getOperation());
                String op = getOperation(val);
                sb.append(op);
            }
            return sb.toString();
        }
        if(move.getPiece() != null) {
            sb.append(getPiece(booleansToByte(move.getPiece())));
        }
        if(move.getStartPosition() != null) {
            sb.append(getStartingPosition(booleansToByte(move.getStartPosition())));
        }
        if(move.isCaptures()) {
            sb.append("x");
        }
        if(move.getTargetFile() != null) {
            sb.append(getFile(booleansToByte(move.getTargetFile())));
        }

        if(move.getTargetRow()!= null) {
            sb.append(getRank(booleansToByte(move.getTargetRow())));
        }
        boolean appendMate = false;
        boolean appendCheck = false;
        if(move.getOperation()!= null) {
            byte val = booleansToByte(move.getOperation());
            String op = getOperation(val);

            if(op.equalsIgnoreCase("?")) {
                sb.append("=");
                appendMate = true;
                appendCheck = false;
            }

            if(op.equalsIgnoreCase("*")) {
                sb.append("=");
                appendMate = false;
                appendCheck = true;
            }

            if(op.equalsIgnoreCase("+") || op.equalsIgnoreCase("#") || op.equalsIgnoreCase("=")) {
                sb.append(op);
                appendMate = false;
                appendCheck = false;
            }
        }

        if(move.getPromotedPiece() != null) {
            sb.append(getPiece(booleansToByte(move.getPromotedPiece())));
        }

        if(appendCheck) {
            sb.append("+");
        }

        if(appendMate) {
            sb.append("#");
        }
        return sb.toString();
    }


    private static TinyMove getSimplePieceMoveWithStartingPosition(String moveStr, boolean capture) {
        TinyMove move = new TinyMove();
        byte piece = 0, startingPosition = 0, file = 0, rank = 0;
        piece = getPiece(moveStr.charAt(0));
        startingPosition = getStartingPosition(moveStr.charAt(1));
        if(!capture) {
            file = getFile(moveStr.charAt(2));
            rank = getRank(moveStr.charAt(3));
        } else {
            file = getFile(moveStr.charAt(3));
            rank = getRank(moveStr.charAt(4));
            move.setCaptures(capture);
        }
        move.setHasStartingPosition(true);
        move.setStartPosition(getBooleanArray(startingPosition,(byte)4));
        move.setPiece(getBooleanArray(piece, (byte)3));
        move.setTargetFile(getBooleanArray(file, (byte)3));
        move.setTargetRow(getBooleanArray(rank, (byte)3));
        setOperation(moveStr, move);
        return move;
    }




    private static TinyMove getSimplePieceMove(String moveStr, boolean capture) {
        TinyMove move = new TinyMove();
        byte piece = 0, file = 0, rank = 0;
        piece = getPiece(moveStr.charAt(0));
        if(!capture) {
            file = getFile(moveStr.charAt(1));
            rank = getRank(moveStr.charAt(2));
        } else {
            file = getFile(moveStr.charAt(2));
            rank = getRank(moveStr.charAt(3));
            move.setCaptures(capture);
        }

        move.setPiece(getBooleanArray(piece, (byte)3));
        move.setTargetFile(getBooleanArray(file, (byte)3));
        move.setTargetRow(getBooleanArray(rank, (byte)3));
        setOperation(moveStr, move);
        return move;
    }

    private static TinyMove getPawnMoveWithPromotion(String moveStr) {
        TinyMove move = new TinyMove();
        byte piece = getPiece('P');
        byte file = getFile(moveStr.charAt(0));
        byte rank = getRank(moveStr.charAt(1));
        byte promotedPiece = getPiece(moveStr.charAt(3));
        move.setTargetFile(getBooleanArray(file, (byte)3));
        move.setTargetRow(getBooleanArray(rank, (byte)3));
        move.setPromotedPiece(getBooleanArray(promotedPiece, (byte) 3));
        move.setPiece(getBooleanArray(piece,(byte) 3));
        setOperation(moveStr, move);
        return move;
    }

    private static TinyMove getPawnCaptureMoveWithPromotion(String moveStr) {
        TinyMove move = new TinyMove();
        move.setCaptures(true);
        byte piece = getPiece('P');
        byte startingPosition = getStartingPosition(moveStr.charAt(0));
        byte file = getFile(moveStr.charAt(2));
        byte rank = getRank(moveStr.charAt(3));
        byte promotedPiece = getPiece(moveStr.charAt(5));
        move.setHasStartingPosition(true);
        move.setTargetFile(getBooleanArray(file, (byte)3));
        move.setTargetRow(getBooleanArray(rank, (byte)4));
        move.setStartPosition(getBooleanArray(startingPosition, (byte)3));
        move.setPromotedPiece(getBooleanArray(promotedPiece, (byte)3));
        move.setPiece(getBooleanArray(piece, (byte)3));
        setOperation(moveStr, move);
        return move;
    }


    private static TinyMove getSimplePawnCaptureMove(String moveStr) {
        TinyMove move = new TinyMove();
        byte piece = getPiece('P');
        byte startingPosition = getStartingPosition(moveStr.charAt(0));
        byte file = getFile(moveStr.charAt(2));
        byte rank = getRank(moveStr.charAt(3));
        move.setCaptures(true);
        move.setHasStartingPosition(true);
        move.setStartPosition(getBooleanArray(startingPosition, (byte)4));
        move.setTargetFile(getBooleanArray(file, (byte)3));
        move.setTargetRow(getBooleanArray(rank, (byte)3));
        move.setPiece(getBooleanArray(piece, (byte)3));
        setOperation(moveStr, move);
        return move;
    }


    private static TinyMove getSimplePawnMove(String moveStr) {
        TinyMove move = new TinyMove();
        byte piece = getPiece('P');
        byte file = getFile(moveStr.charAt(0));
        byte rank = getRank(moveStr.charAt(1));
        move.setTargetFile(getBooleanArray(file, (byte)3));
        move.setTargetRow(getBooleanArray(rank, (byte)3));
        move.setPiece(getBooleanArray(piece, (byte)3));
        setOperation(moveStr, move);
        return move;
    }

    private static void setOperation(String moveStr, TinyMove move) {
        if(moveStr.contains("+") && moveStr.contains("=")) {
            move.setOperation(getBooleanArray(getOperation('*'), (byte)3));
            return;
        }
        if(moveStr.contains("#") && moveStr.contains("=")) {
            move.setOperation(getBooleanArray(getOperation('?'), (byte)3));
            return;
        }

        if(moveStr.contains("+")) {
            move.setOperation(getBooleanArray(getOperation('+'), (byte)3));
            return;
        }

        if(moveStr.contains("#")) {
            move.setOperation(getBooleanArray(getOperation('#'), (byte)3));
            return;
        }

        if(moveStr.contains("=")) {
            move.setOperation(getBooleanArray(getOperation('='), (byte)3));
            return;
        }
    }

    private static String getOperation(byte val) {
        switch (val) {
            case 1 :
                return "+";
            case 2 :
                return "#";
            case 3 :
                return "=";
            case 4 :
                return "*";
            case 5 :
                return "?";
        }
        throw new IllegalArgumentException("Incorrect Operation given:" + val);
    }

    private static byte getOperation(char op){
        switch (op) {
            case '+':
                return 1;
            case '#':
                return 2;
            case '=':
                return 3;
            case '*': //for =+
                return 4;
            case '?'://for =#
                return 5;
        }
        throw new IllegalArgumentException("Incorrect Operation given:" + op);
    }

    private static byte getStartingPosition(char pos) {
        switch (pos) {
            case 'a':
                return  a;
            case 'b':
                return  b;
            case 'c':
                return  c;
            case 'd':
                return  d;
            case 'e':
                return  e;
            case 'f':
                return  f;
            case 'g':
                return  g;
            case 'h':
                return  h;
            case '1':
                return  8;
            case '2':
                return  9;
            case '3':
                return  10;
            case '4':
                return  11;
            case '5':
                return  12;
            case '6':
                return  13;
            case '7':
                return  14;
            case '8':
                return  15;
        }
        throw new IllegalArgumentException("Incorrect position given:" + pos);
    }


    private static byte getRank(char charAt) {
        switch (charAt) {
            case '1':
                return  ONE;
            case '2':
                return  TWO;
            case '3':
                return  THREE;
            case '4':
                return  FOUR;
            case '5':
                return  FIVE;
            case '6':
                return  SIX;
            case '7':
                return  SEVEN;
            case '8':
                return  EIGHT;
        }
        throw new IllegalArgumentException("Incorrect Rank given:" + charAt);
    }

    private static byte getFile(char file) {
        switch (file) {
            case 'a':
                return  a;
            case 'b':
                return  b;
            case 'c':
                return  c;
            case 'd':
                return  d;
            case 'e':
                return  e;
            case 'f':
                return  f;
            case 'g':
                return  g;
            case 'h':
                return  h;
        }
        throw new IllegalArgumentException("Incorrect file given:" + file);
    }

    private static String getFile(byte val) {
        switch (val) {
            case 0:
                return "a";
            case 1:
                return "b";
            case 2:
                return "c";
            case 3:
                return "d";
            case 4:
                return "e";
            case 5:
                return "f";
            case 6:
                return "g";
            case 7:
                return "h";
        }
        throw new IllegalArgumentException("Incorrect file given:" + val);
    }

    private static String getRank(byte val) {
        switch (val) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            case 4:
                return "5";
            case 5:
                return "6";
            case 6:
                return "7";
            case 7:
                return "8";
        }
        throw new IllegalArgumentException("Incorrect Rank given:" + val);
    }

    private static byte getPiece(char piece) {
        switch (piece) {
            case 'R':
                return  ROOK;
            case 'N':
                return  KNIGHT;
            case 'B':
                return  BISHOP;
            case 'Q':
                return  QUEEN;
            case 'K':
                return  KING;
            case 'P':
                return  PAWN;
        }
        throw new IllegalArgumentException("Incorrect piece given:" + piece);
    }



    private static String getPiece(byte val) {
        switch (val) {
            case 1 :
                return "R";
            case 2 :
                return "N";
            case 3 :
                return "B";
            case 4 :
                return "Q";
            case 5 :
                return "K";
            case 6 :
                return "";
        }
        throw new IllegalArgumentException("not a valid num for piece:" + val);
    }

    private static String getStartingPosition(byte val) {
        switch (val) {
            case 0 :
                return "a";
            case 1 :
                return "b";
            case 2 :
                return "c";
            case 3 :
                return "d";
            case 4 :
                return "e";
            case 5 :
                return "f";
            case 6 :
                return "g";
            case 7 :
                return "h";
            case 8 :
                return "1";
            case 9 :
                return "2";
            case 10 :
                return "3";
            case 11 :
                return "4";
            case 12 :
                return "5";
            case 13 :
                return "6";
            case 14 :
                return "7";
            case 15 :
                return "8";
        }
        throw new IllegalArgumentException("not a valid num for piece:" + val);
    }


    private static byte booleansToByte(boolean[] arr){
        int n = 0;
        for (boolean b : arr)
            n = (n << 1) | (b ? 1 : 0);
        return (byte) n;
    }

    private static int booleansToInt(boolean[] arr){
        int n = 0;
        for (boolean b : arr)
            n = (n << 1) | (b ? 1 : 0);
        return n;
    }

    private static boolean isArrayEmpty(boolean[] arr) {
        boolean notEmpty = false;
        for(boolean b : arr) {
            notEmpty = notEmpty | b;
            if(notEmpty) {
                return false;
            }
        }
        return true;
    }



    private static void prepareMOveEncodingMap() {
        String[] files = {"a","b","c","d","e","f","g","h"};
        String[] ranks = {"1","2","3","4","5","6","7","8"};
        String[] pieces = {"R","N","B","Q","K"};
        String[] operation = {"+","=","#"};
        byte counter = Byte.MIN_VALUE;

        //castle moves
        movePartToNativeEncodingMap.put(O_O, counter); counter++;
        movePartToNativeEncodingMap.put(O_O1, counter); counter++;
        movePartToNativeEncodingMap.put(O_O2, counter); counter++;
        movePartToNativeEncodingMap.put(O_O_O, counter); counter++;
        movePartToNativeEncodingMap.put(O_O_O1,counter); counter++;
        movePartToNativeEncodingMap.put( O_O_O2, counter); counter++;


        //prepare simple pawn moves eg : e4,
        for (int i = 0; i < files.length; i++) {
            for (int j = 0; j < ranks.length; j++) {
                movePartToNativeEncodingMap.put(files[i]+ranks[j], counter);
                counter++;
            }
        }

        //prepare pawn capture moves eg: ex of exd4
        for (int i = 0; i < files.length; i++) {
            movePartToNativeEncodingMap.put(files[i]+"x", counter);
            counter++;
        }

        //prepare piece capture moves eg: Nx of Nxg6
        for (int i = 0; i < pieces.length; i++) {
            movePartToNativeEncodingMap.put(pieces[i]+"x", counter);
            counter++;
        }

        //prepare piece with rank position eg. N8 of N8g6
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < ranks.length; j++) {
                movePartToNativeEncodingMap.put(pieces[i]+ranks[j], counter);
                counter++;
            }
        }

        //prepare piece with file position eg. Nh of Nhg6
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < files.length; j++) {
                movePartToNativeEncodingMap.put(pieces[i]+files[j], counter);
                counter++;
            }
        }

        //prepare piece capture moves of piece with file eg: Nhx of Nhxg6
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < files.length; j++) {
                movePartToNativeEncodingMap.put(pieces[i]+files[j]+"x", counter);
                counter++;
            }
        }

        //prepare piece with rank position with capture eg. N8x of N8xg6
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < ranks.length; j++) {
                movePartToNativeEncodingMap.put(pieces[i]+ranks[j]+"x", counter);
                counter++;
            }
        }

        //prepare operations
        for (int i = 0; i < operation.length; i++) {
            movePartToNativeEncodingMap.put(operation[i],counter);
            counter++;
        }

        //prepare pieces
        for (int i = 0; i < pieces.length; i++) {
            movePartToNativeEncodingMap.put(pieces[i], counter);
            counter++;
        }
        System.out.println("Total Size:" + movePartToNativeEncodingMap.size());
        List<Byte> nums = new ArrayList<>();
        for(String key : movePartToNativeEncodingMap.keySet()) {
            nums.add(movePartToNativeEncodingMap.get(key));
        }
        Collections.sort(nums);
        for (byte i = -128; i < 127; i++) {
            if(!nums.contains(i) && i != DETAIL_MOVE_SEPARATOR && i != GAME_SEPARATOR) {
                System.out.println(i);
            }
        }
        for(Map.Entry<String, Byte> entry : movePartToNativeEncodingMap.entrySet()) {
            //byte[] bytes = new byte[1];
            //bytes[0] = entry.getValue();
            //System.out.println(entry.getKey()+":"+entry.getValue()+":" + new String(bytes, StandardCharsets.UTF_8));
            nativeToMovePartDecodingMap.put(entry.getValue(),entry.getKey());
        }
    }

    public static byte[] getNativeMove(TinyMove move) {
        List<Byte> bytes = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if(move.getPiece() == null) {
            if(move.isCastle()) {
                sb.append("O-O-O");
            } else {
                sb.append("O-O");
            }
            if(move.getOperation() != null) {
                byte val = booleansToByte(move.getOperation());
                String op = getOperation(val);
                sb.append(op);
            }
            String castleMove =  sb.toString();
            byte b = encodeToNativeCode(castleMove);
            byte[] oneByte = new byte[1];
            oneByte[0] = b;
            return oneByte;
        }


        sb = new StringBuilder();
        String piece = getPiece(booleansToByte(move.getPiece()));
        if(Strings.isNullOrEmpty(piece)) {
            if(move.isHasStartingPosition()) {
                //if pawn move has starting position then it will always be a capture move
                sb.append(getStartingPosition(booleansToByte(move.getStartPosition())));
                sb.append("x");
                String pawnStartLocation = sb.toString();
                bytes.add(encodeToNativeCode(pawnStartLocation));
                sb = new StringBuilder();
            }

            //prepare target square
            sb.append(getFile(booleansToByte(move.getTargetFile())));
            sb.append(getRank(booleansToByte(move.getTargetRow())));
            String targetSquare = sb.toString();
            bytes.add(encodeToNativeCode(targetSquare));
            //check for promotion or check or mate or check with promotion or mate with promotion
            if(move.getOperation() != null) {
                byte val = booleansToByte(move.getOperation());
                String op = getOperation(val);
                //check promotions
                if(op.equalsIgnoreCase("?") || op.equalsIgnoreCase("*") || op.equalsIgnoreCase("=")) {
                    String promotedPiece = getPiece(booleansToByte(move.getPromotedPiece()));
                    bytes.add(encodeToNativeCode("="));
                    bytes.add(encodeToNativeCode(promotedPiece));
                    if(op.equalsIgnoreCase("?")) {
                        bytes.add(encodeToNativeCode("#"));
                    }
                    if(op.equalsIgnoreCase("*")) {
                        bytes.add(encodeToNativeCode("+"));
                    }
                } else {
                    if(op.equalsIgnoreCase("+")) {
                        bytes.add(encodeToNativeCode("+"));
                    } else {
                        bytes.add(encodeToNativeCode("#"));
                    }
                }
                if(!move.isHasStartingPosition()) {
                    byte[] moveByte = getBytes(bytes);
                    return moveByte;
                }
            }
            byte[] moveByte = getBytes(bytes);
            return moveByte;

        } else {
            sb.append(piece);
            if(move.isHasStartingPosition()) {
                sb.append(getStartingPosition(booleansToByte(move.getStartPosition())));
            }
            if(move.isCaptures()) {
                sb.append("x");
            }
            String pieceMove = sb.toString();
            bytes.add(encodeToNativeCode(pieceMove));
            sb = new StringBuilder();
            //prepare target square
            sb.append(getFile(booleansToByte(move.getTargetFile())));
            sb.append(getRank(booleansToByte(move.getTargetRow())));
            String targetSquare = sb.toString();
            bytes.add(encodeToNativeCode(targetSquare));
            sb = new StringBuilder();
            if(move.getOperation()!= null) {
                byte val = booleansToByte(move.getOperation());
                String op = getOperation(val);
                sb.append(op);
                String operation = sb.toString();
                bytes.add(encodeToNativeCode(operation));
            }
            //Collections.reverse(bytes);
            byte[] pieceMoveBytes = getBytes(bytes);
            return pieceMoveBytes;
        }
    }

    public static TinyMove getTinyMoveFromNativeMove(byte[] encodedMove) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encodedMove.length; i++) {
            sb.append(decodeNativeCode(encodedMove[i]));
        }
        return getTinyMove(sb.toString());
    }

    private static byte[] getBytes(List<Byte> bytes) {
        byte[] moveByte = new byte[bytes.size()];
        for (int i = 0; i < moveByte.length; i++) {
            moveByte[i] = bytes.get(i);
        }
        return moveByte;
    }

    private static String decodeNativeCode(byte code) {
        if(movePartToNativeEncodingMap.size() == 0) {
            prepareMOveEncodingMap();
        }
        return nativeToMovePartDecodingMap.get(code);
    }

    private static byte encodeToNativeCode(String code) {
        if(movePartToNativeEncodingMap.size() == 0) {
            prepareMOveEncodingMap();
        }
        if(movePartToNativeEncodingMap.containsKey(code)) {
            return movePartToNativeEncodingMap.get(code);
        }
        throw new IllegalArgumentException("No such code is known:" + code);
    }

}