package com.scriptchess.models;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 11/09/22
 */

@Data
public class Tournament {
    private String name;
    private Date startDate;
    private List<Player> players;
    private String site;
    private int rounds;
}