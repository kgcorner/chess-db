package com.scriptchess.services;


import com.scriptchess.annotations.ChessDbService;
import com.scriptchess.data.*;
import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.exceptions.DAOException;
import com.scriptchess.exceptions.UnSupportedPgn;
import com.scriptchess.models.*;
import com.scriptchess.services.parsers.PGNProcessorFactory;
import com.scriptchess.services.parsers.PgnProcessor;
import com.scriptchess.util.DateUtil;
import com.scriptchess.util.FenCreator;
import com.scriptchess.util.FenDbHandler;
import com.scriptchess.util.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * Description : Service for All db operations
 * Author: kumar
 * Created on : 14/09/22
 */

@Component
public class ChessService {
    private static final Logger LOGGER = LogManager.getLogger(ChessService.class);

    @Autowired
    private GamesDao gamesDao;

    @Autowired
    private IndexDao indexDao;

    @Autowired
    private PlayersDao playersDao;

    @Autowired
    private TournamentsDao tournamentsDao;

    @Autowired
    private MovesDao movesDao;

    @Value("${games.path}")
    private String gamesPath;

    @Value("${fens.path}")
    private String fensPath;

    @Autowired
    private FenDbHandler fenDbHandler;

    /**
     * Saves the game
     * @param pgn
     * @return
     * @throws UnSupportedPgn
     */
    public Game saveGame(String pgn) throws UnSupportedPgn {
        PgnProcessor processor = PGNProcessorFactory.getProcessor(pgn);
        if(processor == null)
            throw new UnSupportedPgn();
        Game game = processor.parsePgn(pgn);
        try {
            int index = gamesDao.saveGame(game);
            if(game.getTournament() != null) {
                indexDao.saveTournamentIndex(game.getTournament(), index);
            }
            if(game.getWhitePlayer() != null && !Strings.isNullOrEmpty(game.getWhitePlayer().getFideId())) {
                indexDao.savePlayersIndex(game.getWhitePlayer(), index);
            }

            if(game.getBlackPlayer() != null && !Strings.isNullOrEmpty(game.getBlackPlayer().getFideId())) {
                indexDao.savePlayersIndex(game.getBlackPlayer(), index);
            }

            indexDao.saveMoveSequence(game.getMoves(), index);
            movesDao.saveMoves(game, index);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return game;
    }

    /**
     * Get the games played by given player
     * @param fideId
     * @return
     */
    public List<Game> getGamesOfPlayer(String fideId) {
        List<Game> games = Collections.emptyList();
        try {
            Player player = playersDao.getPlayer(fideId);
            List<Integer> gamesOfPlayer = indexDao.getGamesOfPlayer(player);
            games = gamesDao.getGames(gamesOfPlayer);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return games;
    }

    /**
     * returns games played in choosen tournament and year
     * @param tournamentName
     * @param yearOfTournament
     * @return
     */
    public List<Game> getGamesOfTournament(String tournamentName, int yearOfTournament) {
        List<Game> games = Collections.emptyList();
        try {
            Tournament tournament = new Tournament();
            tournament.setYear(yearOfTournament);
            tournament.setName(tournamentName);
            List<Integer> gamesOfPlayer = indexDao.getGamesOfTournament(tournament);
            games = gamesDao.getGames(gamesOfPlayer);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return games;
    }

    /**
     * Returns list of known games after certain sequence of moves
     * All Move needs to be in sequence separated by ">" (even between black's and white's move) eg :e4>e5
     * @param moves
     * @return
     */
    public List<Game> getGamesAfterMoves(String moves) {
        List<Game> games = Collections.emptyList();
        if(Strings.isNullOrEmpty(moves) || !moves.contains(">"))
            return Collections.emptyList();
        List<String> movesStrList = Arrays.asList(moves.split(">"));
        List<Move> moveList = new ArrayList<>();
        for(String move : movesStrList) {
            //While querying move sequence doesn't matter as each moves are considered sequentially on db level
            moveList.add(new Move(0, move, 0));
        }
        try {
            List<Integer> gamesFromMove = indexDao.getGamesFromMove(moveList);
            games = gamesDao.getGames(gamesFromMove);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return games;
    }

    /**
     * Returns next moves and number of times it was played after given move sequence.
     * All Move needs to be in sequence separated by ">" (even between black's and white's move) eg :e4>e5
     * @param moves
     * @return
     */
    public Map<String, Integer> getMovesAndGameCountAfterMove(String moves) {
        Map<String, Integer> map = Collections.emptyMap();
        if(Strings.isNullOrEmpty(moves) || !moves.contains(">"))
            return Collections.emptyMap();
        List<String> movesStrList = Arrays.asList(moves.split(">"));
        List<Move> moveList = new ArrayList<>();
        for(String move : movesStrList) {
            //While querying move sequence doesn't matter as each moves are considered sequentially on db level
            moveList.add(new Move(0, move, 0));
        }
        try {
            map = movesDao.getMovesAndGameCountAfter(moveList);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return map;
    }

    /**
     * Returns moves and number of time the move was played on any given position
     * @param fen
     * @return
     * @throws ChessDbException
     */
    public Map<String, MoveDetails> getMovesForPosition(String fen) throws ChessDbException {
        Map<String, MoveDetails> map = new HashMap<>();
        Fen fenModel = FenCreator.findFen(fen, fensPath);
        if(fenModel == null)
            return null;
        for(MoveDetails md : fenModel.getMoveDetails()) {
            md.setCount(md.getBlackWins() + md.getWhiteWins() + md.getDraws());
            map.put(md.getMove(), md);
        }
        return map;
    }

    /**
     * Saves a given player
     * @param player
     * @return
     */
    public Player savePlayer(Player player) {
        try {
            player = playersDao.savePlayer(player);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return player;
    }

    /**
     * get list opf available players
     * @return
     */
    public List<Player> getPlayers() {
        List<Player> playerList = null;
        try {
            playerList = playersDao.getPlayers();
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return playerList;
    }

    /**
     * Update list of Players
     * @param player
     * @param fideId
     * @return
     */
    public Player updatePlayer(Player player, String fideId) {
        try {
            player = playersDao.updatePlayer(player,fideId);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return player;
    }

    /**
     * Delete a player
     * @param fideId
     */
    public void deletePlayer(String fideId) {
        try {
            playersDao.deletePlayer(fideId);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * get details of a particular player
     * @param fideId
     * @return
     */
    public Player getPlayer(String fideId) {
        Player player = null;
        try {
            player = playersDao.getPlayer(fideId);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return player;
    }

    /**
     * Saves the given tournament
     * @param tournament
     * @return
     */
    public Tournament saveTournament(Tournament tournament) {
        try {
            tournament = tournamentsDao.saveTournament(tournament);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return tournament;
    }

    /**
     * deletes the given tournament
     * @param tournament
     */
    public void deleteTournament(Tournament tournament) {
        try {
            tournamentsDao.deleteTournament(tournament);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * returns the list of available tournaments
     * @return
     */
    public List<Tournament> getTournaments() {
        List<Tournament>  tournaments = null;
        try {
            tournaments = tournamentsDao.getTournaments();
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return tournaments;
    }

    /**
     * returns list of tournaments taken place in given year
     * @param year
     * @return
     */
    public List<Tournament> getTournamentsInYear(int year) {
        List<Tournament>  tournaments = null;
        try {
            tournaments = tournamentsDao.getTournamentsInYear(year);
        } catch (DAOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return tournaments;
    }


    /**
     * Writes list of give games on configured path in compressed format
     * @param games list of games
     * @param session game writing session
     * @param gameUpdateCallback callback to get update on game writing process
     * @param fenUpdateCallback  callback to get update on Fen writing process
     * @return
     * @throws ChessDbException
     */
    public List<Game> saveGames(List<Game> games, String session, UpdateCallBack gameUpdateCallback, UpdateCallBack fenUpdateCallback) throws ChessDbException {
        List<String> ids = new ArrayList<>();
        long idGenerationStart = System.currentTimeMillis();
        for(Game game : games) {
            String id = Strings.generateId();
            while (ids.contains(id)) {
                id = Strings.generateId();
            }
            game.setGameId(id);
            ids.add(id);
        }
        long idGenerationEnd = System.currentTimeMillis();
        LOGGER.info("ID generation took " + DateUtil.getTimeDiff(idGenerationEnd, idGenerationStart));
        long startPgnCompression = System.currentTimeMillis();
        for(Game game : games) {
            String idToPath = Strings.createPathFromId(game.getGameId());
            String path = gamesPath + File.separator + idToPath;
            if(path.endsWith(File.separator)) {
                path = path.substring(0, path.length() -1);
            }
            PgnCompressorDecompressor.writeGame(game, path);
            if(gameUpdateCallback != null)
                gameUpdateCallback.updated(game);
        }
        long endPgnCompression = System.currentTimeMillis();
        LOGGER.debug("PGN Compression for all games took :" + (endPgnCompression - startPgnCompression ) +"ms");
        fenDbHandler.createFens(games, session, fenUpdateCallback);
        LOGGER.debug("Total byte written for fen: " + FenCreator.byteWritten);
        return games;
    }

    public Game getGame(String gameId) {
        String path = Strings.createPathFromId(gameId);
        path = gamesPath + File.separator + path;
        if(path.endsWith(File.separator)) {
            path = path.substring(0, path.length() -1);
        }
        List<Game> games = PgnCompressorDecompressor.readGames(path);

        if (games != null && games.size() > 0) {
            return games.get(0);
        }

        return null;
    }

    public Fen findFen(String fen) {
        return FenCreator.findFen(fen, fensPath);
    }
}