package com.scriptchess.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 14/09/22
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Move {
    private int moveNumber;
    private String move;
    private int timeTakenInSec;
}