package com.scriptchess.models;


import com.scriptchess.util.Strings;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 11/09/22
 */

@Data
@NoArgsConstructor
public class Game {
    private static final String PGN_FORMAT = "[Event \"{event}\"]\n" +
        "[Site \"{site}\"]\n" +
        "[Date \"{date}\"]\n" +
        "[Round \"?\"]\n" +
        "[White \"{whitePlayerName}\"]\n" +
        "[Black \"{blackPlayerName}\"]\n" +
        "[Result \"{result}\"]\n" +
        "[WhiteElo \"{whiteElo}\"]\n" +
        "[BlackElo \"{blackElo}\"]\n" +
        "[WhiteFideId \"{whiteFideId}\"]\n" +
        "[BlackFideId \"{blackFideId}\"]\n" +
        "[Tournament \"{tournament}\"]\n" +
        "{otherDetails}\n"+
        "\n" +
        "{pgn} {result}" +
        "\n";
    private static final String EVENT_PLACEHOLDER = "{event}";
    private static final String SITE_PLACEHOLDER = "{site}";
    private static final String DATE_PLACEHOLDER = "{date}";
    private static final String WHITE_PLAYER_NAME_PLACEHOLDER = "{whitePlayerName}";
    private static final String BLACK_PLAYER_NAME_PLACEHOLDER = "{blackPlayerName}";
    private static final String WHITE_ELO_PLACEHOLDER = "{whiteElo}";
    private static final String BLACK_ELO_PLACEHOLDER = "{blackElo}";
    private static final String WHITE_FIDE_ID_PLACEHOLDER = "{whiteFideId}";
    private static final String BLACK_FIDE_ID_PLACEHOLDER = "{blackFideId}";
    private static final String TOURNAMENT_PLACEHOLDER = "{tournament}";
    private static final String RESULT_PLACEHOLDER = "{result}";
    private static final String ROUND_PLACEHOLDER = "{round}";
    private static final String OTHER_DETAILS_PLACEHOLDER = "{otherDetails}";
    private static final String DETAILS_PLACEHOLDER = "[{key} \"{value}\"]";
    private static final String KEY_PLACEHOLDER = "{key}";
    private static final String VALUE_PLACEHOLDER = "{value}";
    private static final String PGN_PLACEHOLDER = "{pgn}";

    private List<Move> moves;
    private String event;
    private String site;
    private Date date;
    private int round;
    private Player whitePlayer;
    private Player blackPlayer;
    private String result;
    private boolean whiteWinner;
    private Tournament tournament;
    private Map<String, String> otherDetails = new HashMap<>();

    public String exportInPgn() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.mm.dd");
        String pgn = PGN_FORMAT.replace(EVENT_PLACEHOLDER, getStringOrNull(event))
            .replace(SITE_PLACEHOLDER, getStringOrNull(site))
            .replace(DATE_PLACEHOLDER, getStringOrNull(sdf.format(date)))
            .replace(WHITE_PLAYER_NAME_PLACEHOLDER, getStringOrNull(whitePlayer.getName()))
            .replace(BLACK_PLAYER_NAME_PLACEHOLDER, getStringOrNull(blackPlayer.getName()))
            .replace(RESULT_PLACEHOLDER, getStringOrNull(result))
            .replace(RESULT_PLACEHOLDER, getStringOrNull(result))
            .replace(WHITE_ELO_PLACEHOLDER, getStringOrNull(whitePlayer.getElo()))
            .replace(BLACK_ELO_PLACEHOLDER, getStringOrNull(blackPlayer.getElo()))
            .replace(WHITE_FIDE_ID_PLACEHOLDER, getStringOrNull(whitePlayer.getFideId()))
            .replace(BLACK_FIDE_ID_PLACEHOLDER, getStringOrNull(blackPlayer.getFideId()))
            .replace(TOURNAMENT_PLACEHOLDER, getStringOrNull(tournament != null ?tournament.getName() : null))
            .replace(ROUND_PLACEHOLDER, getStringOrNull(round+""));
        String otherDetailsStr = "";
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : otherDetails.entrySet()) {
            sb.append(DETAILS_PLACEHOLDER.replace(KEY_PLACEHOLDER, entry.getKey()).replace(VALUE_PLACEHOLDER, entry.getValue()));
            sb.append("\n");
        }
        otherDetailsStr = sb.toString();

        String moveStr = "";
        int currentMove = 0;
        sb = new StringBuilder();
        if(moves != null && moves.size() > 0) {
            for(Move move : moves) {
                if(currentMove == move.getMoveNumber()) {
                    sb.append(" " + move.getMove() +" ");
                } else {
                    sb.append(move.getMoveNumber()+". " + move.getMove());
                    currentMove = move.getMoveNumber();
                }
            }
        }
        moveStr = sb.toString().trim();
        pgn = pgn.replace(PGN_PLACEHOLDER, moveStr);
        pgn = pgn.replace(OTHER_DETAILS_PLACEHOLDER, otherDetailsStr);
        return pgn;
    }

    private CharSequence getStringOrNull(double elo) {
        String val = elo + "";
        if(val.endsWith(".0"))
            return val.substring(0, val.indexOf("."));
        return val;
    }

    private String getStringOrNull(String val) {
        return Strings.isNullOrEmpty(val) ? "?" : val;
    }
}