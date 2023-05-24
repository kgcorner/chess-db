package com.scriptchess.models;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 29/10/22
 */

@Getter
@Setter
public class Fen {
    private List<MoveDetails> moveDetails;
    private List<String> gameIds;
    private String fenString;
    private static String FEN_MOVE_DETAIL_SEPARATOR = ":";
    private static String MOVE_DETAILS_SEPARATOR = "|";
    private static String MOVE_DETAILS_GAME_ID_SEPARATOR = "^";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fenString);
        sb.append(FEN_MOVE_DETAIL_SEPARATOR);
        for(int i=0;i < moveDetails.size(); i++) {
            MoveDetails md = moveDetails.get(i);
            StringBuilder tmpSb = new StringBuilder();
            tmpSb.append(md.getMove());
            tmpSb.append(",");
            tmpSb.append(md.getWhiteWins());
            tmpSb.append(",");
            tmpSb.append(md.getBlackWins());
            tmpSb.append(",");
            tmpSb.append(md.getDraws());
            sb.append(tmpSb.toString());
            if(i < moveDetails.size() -1)
                sb.append(MOVE_DETAILS_SEPARATOR);
        }
        sb.append(MOVE_DETAILS_GAME_ID_SEPARATOR);
        for (int i = 0; i < gameIds.size(); i++) {
            sb.append(gameIds.get(i));
            if(i < gameIds.size() -1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Fen) {
            return ((Fen) obj).fenString.equals(fenString);
        } else {
            return false;
        }
    }
}