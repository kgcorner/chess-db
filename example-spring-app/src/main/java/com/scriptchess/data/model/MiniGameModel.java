package com.scriptchess.data.model;


import java.util.Date;
import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

public class MiniGameModel {
    private String gameId;
    private String whitePlayer;
    private String blackPlayer;
    private String result;
    private String eco;
    private String tournament;
    private Date date;
    private int moveCount;
    private String md5;
    private List<GameCategory> categories;
    private List<SacrificeModel> sacrifices;
    private Date createdOn;
    private boolean partial;
    private boolean categorized;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(String whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<GameCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<GameCategory> categories) {
        this.categories = categories;
    }

    public List<SacrificeModel> getSacrifices() {
        return sacrifices;
    }

    public void setSacrifices(List<SacrificeModel> sacrifices) {
        this.sacrifices = sacrifices;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public boolean isCategorized() {
        return categorized;
    }

    public void setCategorized(boolean categorized) {
        this.categorized = categorized;
    }
}