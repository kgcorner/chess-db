package com.scriptchess.data.model;

import java.io.Serializable;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

class SacrificeModel {
    private String id;
    private String fen;
    private PIECE piece;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public PIECE getPiece() {
        return piece;
    }

    public void setPiece(PIECE piece) {
        this.piece = piece;
    }
}