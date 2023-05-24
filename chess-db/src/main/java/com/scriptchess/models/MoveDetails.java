package com.scriptchess.models;


import lombok.Getter;
import lombok.Setter;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 29/10/22
 */

@Getter
@Setter
public class MoveDetails {
    private String move;
    private int count;
    private int whiteWins;
    private int blackWins;
    private int draws;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MoveDetails) {
            return ((MoveDetails) obj).move.equals(move);
        } else {
            return false;
        }
    }
}