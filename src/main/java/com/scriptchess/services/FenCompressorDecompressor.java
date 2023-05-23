package com.scriptchess.services;


import com.scriptchess.util.FenCreator;
import com.scriptchess.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description : Compresses a FEN into binary format such that it takes less space while storing in physical file.
 * Author: kumar
 * Created on : 27/04/23
 */

public class FenCompressorDecompressor {
    //colour of the piece will be decided by the first bit
    //0 black 1 white
    //pawn value 0
    private static final Boolean[] BLACK_PAWN = {false, false, false, false};
    private static final Boolean[] WHITE_PAWN = {true, false, false, false};
    //rook value 1
    private static final Boolean[] BLACK_ROOK = {false, false, false, true};
    private static final Boolean[] WHITE_ROOK = {true, false, false, true};

    //knight value 2
    private static final Boolean[] BLACK_KNIGHT = {false, false, true, false};
    private static final Boolean[] WHITE_KNIGHT = {true, false, true, false};

    // bishop value 3
    private static final Boolean[] BLACK_BISHOP = {false, false, true, true};
    private static final Boolean[] WHITE_BISHOP = {true, false, true, true};

    //queen value 4
    private static final Boolean[] BLACK_QUEEN = {false, true, false, false};
    private static final Boolean[] WHITE_QUEEN = {true, true, false, false};

    //king value 5
    private static final Boolean[] BLACK_KING = {false, true, false, true};
    private static final Boolean[] WHITE_KING = {true, true, false, true};

    private static final boolean WHITE_TO_PLAY = true;
    private static final boolean BLACK_TO_PLAY = false;
    private static final Boolean[] NO_CASTLE = {false, false};
    private static final Boolean[] SHORT_CASTLE = {false, true};
    private static final Boolean[] LONG_CASTLE = {true, false};
    private static final Boolean[] BOTH_CASTLE = {true, true};
    private static final Boolean[] tmp = new Boolean[0];
    public static final byte[] FEN_MOVE_SEPARATOR = "$^".getBytes(); //separator that separates Fen string and Moves
    public static final byte[] FEN_STRING_SEPARATOR = "^^^".getBytes(); //separator that separates full Fen strings
    public static final String FEN_MOVE_DETAIL_SEPARATOR = "|"; //separator that separates Fen string and Moves
    private static final String MOVE_DETAILS_SEPARATOR = "|";
    private static final String MOVE_DETAILS_GAME_ID_SEPARATOR = "^";
    private static List<Boolean> encodedFen = new ArrayList<>();

    /**
     * Returns the fen in string format from the given list of compressed fens if given fen is available in the list
     * @param fenStringList
     * @param fenToMatch
     * @return
     */
    public static String getMatchingFenString(List<byte[]> fenStringList, String fenToMatch) {
        List<Boolean> compressedFen =  compressFen(fenToMatch);
        Boolean[] compressedFenArr = new Boolean[compressedFen.size()];
        compressedFenArr = compressedFen.toArray(compressedFenArr);
        byte[] fenBytes = booleanArrayToByteArray(compressedFenArr, false);
        for(byte[] fenStringByteArr :  fenStringList) {
            byte[] fenByteArr = Arrays.copyOfRange(fenStringByteArr, 0, fenBytes.length);
            if(Arrays.equals(fenByteArr, fenBytes)) {
                return getFenMoveStringFromByteArray(fenStringByteArr);
            }
        }
        return null;
    }

    /**
     * Returns Fen in string format by decompressing the given compressed fen
     * @param byteArr
     * @return
     */
    public static String getFenMoveStringFromByteArray(byte[] byteArr) {
        byte[] fenArr = null;
        byte[] moveStringArr = null;
        int fenEndsIndex = 0;
        for (int i = 0; i < byteArr.length -1; i++) {
            byte[] tmp = new byte[FEN_MOVE_SEPARATOR.length];
            for (int j = 0; j < FEN_MOVE_SEPARATOR.length; j++) {
                tmp[j] = byteArr[i+j];
            }
            if(Arrays.equals(tmp, FEN_MOVE_SEPARATOR)) {
                fenEndsIndex = i;
            }
        }
        fenArr = new byte[fenEndsIndex];
        for (int i = 0; i < fenEndsIndex; i++) {
            fenArr[i] = byteArr[i];
        }
        moveStringArr = new byte[byteArr.length - fenEndsIndex - FEN_MOVE_SEPARATOR.length];
        for (int i = fenEndsIndex + FEN_MOVE_SEPARATOR.length; i < byteArr.length; i++) {
            moveStringArr[i - (fenEndsIndex + FEN_MOVE_SEPARATOR.length)] = byteArr[i];
        }
        Boolean[] fenBooleanArr = bytesArrayToBooleansArray(fenArr);
        String fen = decompressFen(Arrays.asList(fenBooleanArr));
        String moveStr = new String(moveStringArr);
        return fen+ new String(FEN_MOVE_SEPARATOR) + moveStr;
    }

    /**
     * Compresses the given fen
     * @param fullFenString
     * @return
     */
    public static byte[] getFenMoveInByteArray(String fullFenString) {
        String[] parts = fullFenString.split(Pattern.quote(new String(FEN_MOVE_SEPARATOR)));
        return getFenMoveInByteArray(parts[0], parts[1]);
    }


    /**
     * Compresses the fen along with move map
     * @param fen
     * @param moveMap
     * @return
     */
    public static byte[] getFenMoveInByteArray(String fen, String moveMap) {
        List<Boolean> compressedFen =  compressFen(fen);
        Boolean[] compressedFenArr = new Boolean[compressedFen.size()];
        compressedFenArr = compressedFen.toArray(compressedFenArr);
        byte[] fenBytes = booleanArrayToByteArray(compressedFenArr, false);
        byte[] moveBytes = moveMap.getBytes();
        byte[] finalByteArray = new byte[fenBytes.length + FEN_MOVE_SEPARATOR.length + moveBytes.length + FEN_STRING_SEPARATOR.length];
        for (int i = 0; i < fenBytes.length; i++) {
            finalByteArray[i] = fenBytes[i];
        }

        for (int i = fenBytes.length; i < fenBytes.length + FEN_MOVE_SEPARATOR.length; i++) {
            finalByteArray[i] = FEN_MOVE_SEPARATOR[i - fenBytes.length];
        }

        for (int i = fenBytes.length + FEN_MOVE_SEPARATOR.length;
             i < fenBytes.length + FEN_MOVE_SEPARATOR.length + moveBytes.length; i++) {
            finalByteArray[i] = moveBytes[i - (fenBytes.length + FEN_MOVE_SEPARATOR.length)];
        }
        for (int i = fenBytes.length + FEN_MOVE_SEPARATOR.length + moveBytes.length;
             i < fenBytes.length + FEN_MOVE_SEPARATOR.length + moveBytes.length + FEN_STRING_SEPARATOR.length; i++) {
            finalByteArray[i] = FEN_STRING_SEPARATOR[i - (fenBytes.length + FEN_MOVE_SEPARATOR.length + moveBytes.length)];
        }

        return finalByteArray;
    }

    /**
     * Decompresses the given fen
     * @param compressedFen
     * @return
     */
    private static String decompressFen(List<Boolean> compressedFen) {
        int colLen = 8;
        int rowLen = 8;
        int emptySquaresCount = 0;
        int pieceCount = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colLen; i++) {
            for (int j = 0; j < rowLen; j++) {
                int index = i* colLen + j;
                if(!compressedFen.get(index)) {
                    emptySquaresCount++;
                } else {
                    if(emptySquaresCount > 0) {
                        sb.append(emptySquaresCount);
                        emptySquaresCount = 0;
                    }
                    Boolean[] tmp = new Boolean[0];

                    List<Boolean> pieceArray = compressedFen.subList(pieceCount * 4 + 64, pieceCount * 4 + 68);
                    Boolean[] booleans = pieceArray.toArray(tmp);
                    pieceCount++;
                    int pieceVal = booleanArrayToNumber(booleans);
                    switch (pieceVal) {
                        case 0:
                            sb.append("p");
                            break;
                        case 1:
                            sb.append("r");
                            break;
                        case 2:
                            sb.append("n");
                            break;
                        case 3:
                            sb.append("b");
                            break;
                        case 4:
                            sb.append("q");
                            break;
                        case 5:
                            sb.append("k");
                            break;
                        case 8:
                            sb.append("P");
                            break;
                        case 9:
                            sb.append("R");
                            break;
                        case 10:
                            sb.append("N");
                            break;
                        case 11:
                            sb.append("B");
                            break;
                        case 12:
                            sb.append("Q");
                            break;
                        case 13:
                            sb.append("K");
                            break;
                    }
                }
            }
            if(emptySquaresCount > 0) {
                sb.append(emptySquaresCount);
                emptySquaresCount = 0;
            }
            if(i!=7)
                sb.append("/");
        }
        sb.append(" ");
        int index = pieceCount * 4 + 64;
        //append color to move
        if(compressedFen.get(index)) {
            sb.append("w ");
        } else {
            sb.append("b ");
        }
        index++;
        //append castle right
        List<Boolean> whiteCastle = compressedFen.subList(index, index+2);
        index+=2;
        List<Boolean> blackCastle = compressedFen.subList(index, index+2);
        index+=2;
        String whiteCastleStr = getCastle(whiteCastle, true);
        String blackCastleStr = getCastle(blackCastle, false);
        String fullCastle = whiteCastleStr + blackCastleStr;
        if(Strings.isNullOrEmpty(fullCastle)) {
            fullCastle = "-";
        }
        sb.append(fullCastle);
        sb.append(" ");
        //append en-passent char
        List<Boolean> enpassentArray = compressedFen.subList(index, index+5);
        index+=5;
        sb.append(getEnpassentValue(enpassentArray));
        sb.append(" ");
        List<Boolean> moveCountArr = compressedFen.subList(index, index+7);
        //append Move count from last pawn move
        Boolean[] booleans = moveCountArr.toArray(tmp);
        int moveCount = booleanArrayToNumber(booleans);
        sb.append(moveCount);
        sb.append(" ");
        index+=7;
        moveCountArr = compressedFen.subList(index, index+8);
        index+=8;
        booleans = moveCountArr.toArray(tmp);
        moveCount = booleanArrayToNumber(booleans);
        sb.append(moveCount);
        return sb.toString();
    }

    private static String getEnpassentValue(List<Boolean> enpassentArray) {
        Boolean[] booleans = enpassentArray.toArray(tmp);
        int enpassentValInt = booleanArrayToNumber(booleans);
        switch (enpassentValInt) {
            case 0:
                return "h3";
            case 1:
                return "g3";
            case 2:
                return "f3";
            case 3:
                return "e3";
            case 4:
                return "d3";
            case 5:
                return "c3";
            case 6:
                return "b3";
            case 7:
                return "a3";
            case 8:
                return "h6";
            case 9:
                return "g6";
            case 10:
                return "f6";
            case 11:
                return "e6";
            case 12:
                return "d6";
            case 13:
                return "c6";
            case 14:
                return "b6";
            case 15:
                return "a6";
            case 16:
                return "-";
        }
        return null;
    }

    private static String getCastle(List<Boolean> castleValue, boolean isWhite) {
        Boolean[] booleans = castleValue.toArray(tmp);
        int castleValInt = booleanArrayToNumber(booleans);
        switch (castleValInt) {
            case 0:
                return "";
            case 1:
                return isWhite ? "K" : "k";
            case 2:
                return isWhite ? "Q" : "q";
            case 3:
                return isWhite ? "KQ" : "kq";
        }
        return null;
    }
    private static List<Boolean> compressFen(String fen) {
        //initializes list with 64 booleans with false value
        //64 boolean represents 64 squares
        //false value on squares represents empty square and true mean square hold a piece
        List<Boolean> booleanFen = initializeFenWithEmptyBoard();

        String[] fenParts = fen.split(" ");
        String boardPart = fenParts[0];
        int index = 0;
        //The loop will assign true to squares that have piece and will append piece value to the boolean list
        for (int i = 0; i < boardPart.length(); i++) {
            char fenChar = boardPart.charAt(i);
            switch (fenChar) {
                case 'p':
                    booleanFen.set(index, true);                    
                    booleanFen.addAll(Arrays.asList(BLACK_PAWN));
                    index++;
                    break;
                case 'P':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(WHITE_PAWN));
                    index++;
                    break;
                case 'r':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(BLACK_ROOK));
                    index++;
                    break;
                case 'R':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(WHITE_ROOK));
                    index++;
                    break;
                case 'n':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(BLACK_KNIGHT));
                    index++;
                    break;
                case 'N':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(WHITE_KNIGHT));
                    index++;
                    break;
                case 'b':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(BLACK_BISHOP));
                    index++;
                    break;
                case 'B':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(WHITE_BISHOP));
                    index++;
                    break;
                case 'q':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(BLACK_QUEEN));
                    index++;
                    break;
                case 'Q':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(WHITE_QUEEN));
                    index++;
                    break;
                case 'k':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(BLACK_KING));
                    index++;
                    break;
                case 'K':
                    booleanFen.set(index, true);
                    booleanFen.addAll(Arrays.asList(WHITE_KING));
                    index++;
                    break;
                case '1':
                    index++;
                    break;
                case '2':
                    index+=2;
                    break;
                case '3':
                    index+=3;
                    break;
                case '4':
                    index+=4;
                    break;
                case '5':
                    index+=5;
                    break;
                case '6':
                    index+=6;
                    break;
                case '7':
                    index+=7;
                    break;
                case '8':
                    index+=8;
                    break;
            }
        }
        //next we append the move bit, true for white to move false for black to move
        if(fenParts[1].equals("w")) {
            booleanFen.add(true);
        } else {
            booleanFen.add(false);
        }

        //next will be assignation of castle right
        if(fenParts[2].equals("-")) {
            booleanFen.addAll(Arrays.asList(NO_CASTLE));
            booleanFen.addAll(Arrays.asList(NO_CASTLE));
        } else {
            Boolean[] whiteCastleVal =  {false, false};
            Boolean[] blackCastleVal =  {false, false};
            for (int i = 0; i < fenParts[2].length(); i++) {
                switch (fenParts[2].charAt(i)) {
                    case 'K':
                        whiteCastleVal = new Boolean[]{whiteCastleVal[0] || false, whiteCastleVal[1] || true};
                        break;
                    case 'Q':
                        whiteCastleVal = new Boolean[]{whiteCastleVal[0] || true, whiteCastleVal[1] || false};
                        break;
                    case 'k':
                        blackCastleVal = new Boolean[]{blackCastleVal[0] || false, blackCastleVal[1] || true};
                        break;
                    case 'q':
                        blackCastleVal = new Boolean[]{blackCastleVal[0] || true, blackCastleVal[1] || false};
                        break;
                }
            }
            booleanFen.addAll(Arrays.asList(whiteCastleVal));
            booleanFen.addAll(Arrays.asList(blackCastleVal));
        }
        //add square for en-passent move
        List<Boolean> enPassentSquareNum = null;
        switch (fenParts[3]) {
            case "h3":
                enPassentSquareNum = decimalToBinary(0, 5);
                break;
            case "g3":
                enPassentSquareNum = decimalToBinary(1, 5);
                break;
            case "f3":
                enPassentSquareNum = decimalToBinary(2, 5);
                break;
            case "e3":
                enPassentSquareNum = decimalToBinary(3, 5);
                break;
            case "d3":
                enPassentSquareNum = decimalToBinary(4, 5);
                break;
            case "c3":
                enPassentSquareNum = decimalToBinary(5, 5);
                break;
            case "b3":
                enPassentSquareNum = decimalToBinary(6, 5);
                break;
            case "a3":
                enPassentSquareNum = decimalToBinary(7, 5);
                break;
            case "h6":
                enPassentSquareNum = decimalToBinary(8, 5);
                break;
            case "g6":
                enPassentSquareNum = decimalToBinary(9, 5);
                break;
            case "f6":
                enPassentSquareNum = decimalToBinary(10, 5);
                break;
            case "e6":
                enPassentSquareNum = decimalToBinary(11, 5);
                break;
            case "d6":
                enPassentSquareNum = decimalToBinary(12, 5);
                break;
            case "c6":
                enPassentSquareNum = decimalToBinary(13, 5);
                break;
            case "b6":
                enPassentSquareNum = decimalToBinary(14, 5);
                break;
            case "a6":
                enPassentSquareNum = decimalToBinary(15,5);
                break;
            case "-":
                enPassentSquareNum = decimalToBinary(16, 5);
                break;
        }
        booleanFen.addAll(enPassentSquareNum);
        //add number of moves until last pawn move
        List<Boolean> movesUntilLastPawnMove = decimalToBinary(Integer.parseInt(fenParts[4]), 7);
        booleanFen.addAll(movesUntilLastPawnMove);
        List<Boolean> moveCount = decimalToBinary(Integer.parseInt(fenParts[5]), 8);
        booleanFen.addAll(moveCount);

        return booleanFen;
    }

    private static List<Boolean> initializeFenWithEmptyBoard() {

        List<Boolean> board = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            board.add(false);
        }
        return board;
    }

    private static List<Boolean> decimalToBinary(int num, int size)
    {
        List<Boolean> booleanNum = new ArrayList<>();
        List<Boolean> tmp = new ArrayList<>();
        // Number should be positive
        while (num > 0) {
            boolean one = num % 2 == 1 ? true : false;
            tmp.add(one);
            num = num / 2;
        }
        if(tmp.size() < size) {
            for (int i = 0; i < size - tmp.size(); i++) {
                booleanNum.add(false);
            }
        }

        booleanNum.addAll(reverseArray(tmp));
        return booleanNum;
    }

    private static List<Boolean> reverseArray(List<Boolean> tmp) {
        List<Boolean> booleanNum = new ArrayList<>();
        for (int i = tmp.size(); i >0 ; i--) {
            booleanNum.add(tmp.get(i-1));
        }
        return booleanNum;
    }

    private static int booleanArrayToNumber(Boolean[] bArr) {
        int n = 0;
        for (boolean b : bArr)
            n = (n << 1) | (b ? 1 : 0);
        return n;
    }

    private static void printBooleanArray(List<Boolean> arr) {
        for(Boolean b : arr) {
            if(b) {
                System.out.print(1);
            } else {
                System.out.print(0);
            }
        }
        System.out.println("");
    }

    private static Boolean[] bytesArrayToBooleansArray(byte[] bytes) {
        Boolean[] result = new Boolean[bytes.length * 8];

        for (int i=0; i<bytes.length; i++) {
            int index = i*8;
            result[index+0] = (bytes[i] & 0x80) != 0;
            result[index+1] = (bytes[i] & 0x40) != 0;
            result[index+2] = (bytes[i] & 0x20) != 0;
            result[index+3] = (bytes[i] & 0x10) != 0;
            result[index+4] = (bytes[i] & 0x8) != 0;
            result[index+5] = (bytes[i] & 0x4) != 0;
            result[index+6] = (bytes[i] & 0x2) != 0;
            result[index+7] = (bytes[i] & 0x1) != 0;
        }

        return result;
    }

    private static byte[] booleanArrayToByteArray(Boolean[] booleans, boolean padValue) {
        Boolean[] paddedBooleans;
        int remainder = booleans.length % 8;

        // Booleans are already divisible by 8, nothing to pad
        if (remainder == 0) {
            paddedBooleans = booleans;
        }
        // Boolean are not divisible by 8, need to pad
        else {
            int padAmount = 8 - remainder;
            paddedBooleans = Arrays.copyOf(booleans, booleans.length + padAmount);

            for (int i=booleans.length; i<paddedBooleans.length; i++) {
                paddedBooleans[i] = padValue;
            }
        }

        // Convert the boolean array into a byte array
        byte[] result = new byte[paddedBooleans.length/8];

        for (int i=0; i<result.length; i++) {
            int index = i*8;
            byte b = (byte)(
                (paddedBooleans[index+0] ? 1<<7 : 0) +
                    (paddedBooleans[index+1] ? 1<<6 : 0) +
                    (paddedBooleans[index+2] ? 1<<5 : 0) +
                    (paddedBooleans[index+3] ? 1<<4 : 0) +
                    (paddedBooleans[index+4] ? 1<<3 : 0) +
                    (paddedBooleans[index+5] ? 1<<2 : 0) +
                    (paddedBooleans[index+6] ? 1<<1 : 0) +
                    (paddedBooleans[index+7] ? 1 : 0));
            result[i] = b;
        }

        return result;
    }

    public static List<String> getFenList(byte[] byteArr) {
        List<String> fenStrings = new ArrayList<>();
        List<byte[]> fensInByteArrayList = FenCreator.splitByteArray(byteArr, FEN_STRING_SEPARATOR);
        for(byte[] fenInByteArr : fensInByteArrayList) {
            fenStrings.add(getFenMoveStringFromByteArray(fenInByteArr));
        }
        return fenStrings;
    }
}