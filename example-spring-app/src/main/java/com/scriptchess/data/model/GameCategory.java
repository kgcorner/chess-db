package com.scriptchess.data.model;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

public enum GameCategory {
    ROOK_ENDGAME, //both players have rook and Pawns
    BISHOP_ENDGAME, //Both players have bishops and pawns
    BISHOP_KNIGHT_ENDGAME, //both players has knight and bishop
    KNIGHT_ENDGAME, //both players has knights and pawns
    PAWN_ENDGAME, //both players has only kings and pawns
    LONG_GAME, // games that has more than 70 moves
    QUEEN_IMBALANCED_GAME, // games in which one player has queen another doesn't
    ROOK_KNIGHT_BISHOP_ENDGAME,// games where one player has rook and another has knight and bishop
    BISHOP_VS_PAWN_ENDGAME, //games where one players has only pawns and another bishop
    KNIGHT_VS_PAWN_ENDGAME, //games where one players has only pawns and another knight
    ROOK_VS_PAWN_ENDGAME, //games where one players has only pawns and another rook
    BISHOP_VS_KNIGHT_ENDGAME, //games where one players bishop and other has knight
    MINIATURE
}