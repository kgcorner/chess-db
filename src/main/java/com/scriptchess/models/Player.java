package com.scriptchess.models;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 11/09/22
 */
@Data
@NoArgsConstructor
public class Player  implements Serializable {
    private String name;
    private String[] alias;
    private Date dob;
    private String website;
    private String federation;
    private String fideId;
    private double elo;
    private List<String> playerIds = new ArrayList<>();
}