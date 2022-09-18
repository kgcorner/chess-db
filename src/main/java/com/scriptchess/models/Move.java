package com.scriptchess.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 14/09/22
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Move  implements Serializable {
    private int moveNumber;
    private String move;
    private int timeTakenInSec;
}