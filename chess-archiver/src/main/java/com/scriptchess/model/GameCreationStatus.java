package com.scriptchess.model;


import java.util.Date;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

public class GameCreationStatus {

    private int totalGameCount;
    private int totalFenCount;
    private int totalCreatedGames;
    private int totalCreatedFens;
    private boolean completed;
    private Date lastGameCreatedOn;
    private Date lastFenCreatedOn;
    private MiniGame lastCreatedGame;
    private String lastCreatedFen;
    private Exception error;
    private boolean failed;
    private long startedAt;
    private long endsAt;

    public int getTotalGameCount() {
        return totalGameCount;
    }

    public void setTotalGameCount(int totalGameCount) {
        this.totalGameCount = totalGameCount;
    }

    public int getTotalFenCount() {
        return totalFenCount;
    }

    public void setTotalFenCount(int totalFenCount) {
        this.totalFenCount = totalFenCount;
    }

    public int getTotalCreatedGames() {
        return totalCreatedGames;
    }

    public void setTotalCreatedGames(int totalCreatedGames) {
        this.totalCreatedGames = totalCreatedGames;
    }

    public int getTotalCreatedFens() {
        return totalCreatedFens;
    }

    public void setTotalCreatedFens(int totalCreatedFens) {
        this.totalCreatedFens = totalCreatedFens;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getLastGameCreatedOn() {
        return lastGameCreatedOn;
    }

    public void setLastGameCreatedOn(Date lastGameCreatedOn) {
        this.lastGameCreatedOn = lastGameCreatedOn;
    }

    public Date getLastFenCreatedOn() {
        return lastFenCreatedOn;
    }

    public void setLastFenCreatedOn(Date lastFenCreatedOn) {
        this.lastFenCreatedOn = lastFenCreatedOn;
    }

    public MiniGame getLastCreatedGame() {
        return lastCreatedGame;
    }

    public void setLastCreatedGame(MiniGame lastCreatedGame) {
        this.lastCreatedGame = lastCreatedGame;
    }

    public String getLastCreatedFen() {
        return lastCreatedFen;
    }

    public void setLastCreatedFen(String lastCreatedFen) {
        this.lastCreatedFen = lastCreatedFen;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(long endsAt) {
        this.endsAt = endsAt;
    }
}