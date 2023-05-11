package com.scriptchess.models;


import com.scriptchess.util.Strings;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 11/09/22
 */
@Getter
@Setter
@NoArgsConstructor
public class Player  implements Serializable {
    private static final long serialVersionUID = 1;
    private String name;
    private String[] alias;
    private Date dob;
    private String website;
    private String federation;
    private String playerId;
    private double elo;
    private String fideId;
    private List<String> playerIds = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Player))
            return false;
        if(obj == null)
            return false;
        if(!Strings.isNullOrEmpty(fideId)) {
            return fideId.equals(((Player)obj).fideId);
        }
        return ((Player)obj).playerId.equalsIgnoreCase(playerId) ;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}