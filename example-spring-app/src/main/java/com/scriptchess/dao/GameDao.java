package com.scriptchess.dao;


import com.scriptchess.data.model.MiniGameModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

@Repository
public class GameDao {
    public List<MiniGameModel> createGames(List<MiniGameModel> models) {
        //Create models in database of your choice for quick reference
        return  models;
    }
}