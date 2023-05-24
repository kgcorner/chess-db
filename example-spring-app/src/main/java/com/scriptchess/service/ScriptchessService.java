package com.scriptchess.service;


import com.scriptchess.dao.GameDao;
import com.scriptchess.data.model.GameCreationStatusClientModel;
import com.scriptchess.data.model.MiniGameModel;
import com.scriptchess.exception.ResourceNotFoundException;
import com.scriptchess.exception.ScriptChessException;
import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.models.*;
import com.scriptchess.services.ChessService;
import com.scriptchess.services.parsers.PGNProcessorFactory;
import com.scriptchess.util.DateUtil;
import com.scriptchess.util.JsonUtil;
import com.scriptchess.util.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

@Service
public class ScriptchessService {
    private static final Logger LOGGER = LogManager.getLogger(ScriptchessService.class);
    public static final String currentSession = null;

    private Map<String, GameCreationStatusClientModel> gameCreationStatusMap;
    private ReadWriteLock readWriteLock = null;

    @Autowired
    private ChessService chessService;

    @Autowired
    private GameDao miniGameDao;

    public ScriptchessService() {
        gameCreationStatusMap = new HashMap<>();
        readWriteLock = new ReentrantReadWriteLock();
    }

    public Game getGame(String gameId) {
        return chessService.getGame(gameId);
    }

    private List<MiniGameModel> saveGameInBulkAtOnce(byte[] bytes, String session) throws ScriptChessException {

        List<Game> games = PGNProcessorFactory.getDefaultProcessor().parseMultiGamePgn(bytes);
        return createAllGames(session, games);
    }

    private List<MiniGameModel> createAllGames(String session, List<Game> games) throws ScriptChessException {
        long saveAllGamesPhStart = System.currentTimeMillis();
        long startDupsCheck = System.currentTimeMillis();
        List<MiniGameModel> existingGames = checkExistingGames(games);
        List<Game> newGames = new ArrayList<>();
        //check for new games

        for(Game game :  games) {
            boolean found = false;
            for(MiniGameModel existingGame : existingGames) {
                if(existingGame.getMd5().equals(game.getMd5())) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                newGames.add(game);
            }
        }
        long endDupsCheck = System.currentTimeMillis();
        LOGGER.info("duplicate check took  " + (endDupsCheck - startDupsCheck) + "ms");
        if(newGames.size() == 0) {
            LOGGER.info("All games are already created");
            completeGameCreationRequest(session);
            return Collections.emptyList();
        } else {
            if(newGames.size() != games.size()) {
                LOGGER.info("found  " + (games.size() - newGames.size()) + " duplicate games. Creating only new games");
            }
        }
        int fenCount = 0;
        for(Game game : games) {
            fenCount += game.getFens().size();
        }
        setTotalNumOfGamesAndFen(session, newGames.size(), fenCount);
        try {
            games = chessService.saveGames(newGames, session, object -> updateLastCreatedGame(session, (Game) object),
                object -> updateLastCreatedFen(session, (List<Fen>) object));
        } catch (ChessDbException e) {
            throw new ScriptChessException(e.getMessage());
        }
        long saveAllGamesPhEnd = System.currentTimeMillis();
        LOGGER.info("saved "+ games.size()+" games in disk in " + DateUtil.getTimeDiff(saveAllGamesPhEnd, saveAllGamesPhStart));

        List<MiniGameModel> miniGameModels = new ArrayList<>();
        Set<Player> players = new HashSet<>();
        Set<Tournament> tournaments = new HashSet<>();
        for(Game game : newGames) {
            MiniGameModel model = new MiniGameModel();
            BeanUtils.copyProperties(game, model);
            model.setMoveCount(game.getMoves().size());
            model.setWhitePlayer(game.getWhitePlayer().getName());
            model.setBlackPlayer(game.getBlackPlayer().getName());
            model.setTournament(game.getTournament().getName());
            model.setPartial(false);
            miniGameModels.add(model);
            players.add(game.getWhitePlayer());
            players.add(game.getBlackPlayer());
            tournaments.add(game.getTournament());

        }
        createPlayers(players);
        createTournaments(tournaments, true);
        long saveGamesInDbStart = System.currentTimeMillis();
        miniGameDao.createGames(miniGameModels);
        long saveGamesInDbEnd = System.currentTimeMillis();
        LOGGER.info("saved "+ miniGameModels.size()+" games in db in " + DateUtil.getTimeDiff(saveGamesInDbEnd, saveGamesInDbStart));
        long end = System.currentTimeMillis();
        LOGGER.info("Created " +  miniGameModels.size()+" games in " + DateUtil.getTimeDiff(end, saveAllGamesPhStart));
        completeGameCreationRequest(session);
        return miniGameModels;
    }

    private void createTournaments(Set<Tournament> tournaments, boolean b) {
        //Create Player in external Database like sql or mongo for quick filter
    }

    private void createPlayers(Set<Player> players) {
        //Create Player in external Database like sql or mongo for quick filter
    }

    private void updateLastCreatedFen(String session, List<Fen> fens) {
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        LOGGER.debug("Took lock for updating last created fens");
        try {
            if (gameCreationStatusMap.containsKey(session)) {
                GameCreationStatusClientModel gameCreationStatus = gameCreationStatusMap.get(session);
                gameCreationStatus.setLastCreatedFen(fens.get(fens.size() - 1).getFenString());
                gameCreationStatus.setTotalCreatedFens(gameCreationStatus.getTotalCreatedFens() + fens.size());
                gameCreationStatus.setLastFenCreatedOn(new Date());
            } else {
                LOGGER.warn("Session : " + session +" not found but called for updating last created fens") ;
            }
        } finally {
            LOGGER.debug("Unlocked after updating last created fens");
            lock.unlock();
        }
    }

    private void updateLastCreatedGame(String session, Game game) {
        Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();
            LOGGER.debug("Took lock for updating last created game");
            if (gameCreationStatusMap.containsKey(session)) {
                MiniGameModel model = new MiniGameModel();
                BeanUtils.copyProperties(game, model);
                model.setMoveCount(game.getMoves().size());
                model.setWhitePlayer(game.getWhitePlayer().getName());
                model.setBlackPlayer(game.getBlackPlayer().getName());
                model.setTournament(game.getTournament().getName());
                GameCreationStatusClientModel gameCreationStatus = gameCreationStatusMap.get(session);
                gameCreationStatus.setLastCreatedGame(model);
                gameCreationStatus.setLastGameCreatedOn(new Date());
                gameCreationStatus.setTotalCreatedGames(gameCreationStatus.getTotalCreatedGames() + 1);
            } else {
                LOGGER.warn("Session : " + session +" not found but called for updating game") ;
            }
        } finally {
            LOGGER.debug("Unlocked after for updating last created game");
            lock.unlock();
        }
    }

    private void setTotalNumOfGamesAndFen(String session, int gameCount, int fenCount) {
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        LOGGER.debug("Took lock for Marking request as completed");
        try {
            if (gameCreationStatusMap.containsKey(session)) {
                gameCreationStatusMap.get(session).setTotalGameCount(gameCount);
                gameCreationStatusMap.get(session).setTotalFenCount(fenCount);
            } else {
                LOGGER.warn("Session : " + session +" not found but called for updating game") ;
            }
        }
        finally {
            LOGGER.debug("Unlocked after updating completed status");
            lock.unlock();

        }
    }

    private void completeGameCreationRequest(String session) {
        Lock lock = readWriteLock.writeLock();
        lock.lock();
        LOGGER.debug("Took lock for Marking request as completed");
        try {
            if (gameCreationStatusMap.containsKey(session)) {
                gameCreationStatusMap.get(session).setCompleted(true);
                gameCreationStatusMap.get(session).setEndsAt(new Date().getTime());
            } else {
                LOGGER.warn("Session : " + session +" not found but called for updating game") ;
            }
        }
        finally {
            LOGGER.debug("Unlocked after updating completed status");
            lock.unlock();

        }
    }

    private List<MiniGameModel> checkExistingGames(List<Game> games) {
        ////Create Player in external Database like sql or mongo for quick filter
        return Collections.emptyList();
    }

    public List<GameCreationStatusClientModel> getAllBulkCreateGameRequests() {
        List<GameCreationStatusClientModel> requests = new ArrayList<>();
        for(Map.Entry<String, GameCreationStatusClientModel> entry : gameCreationStatusMap.entrySet()) {
            requests.add(entry.getValue());
        }
        return requests;
    }

    public GameCreationStatusClientModel getGameCreationStatus(String sessionId) throws ResourceNotFoundException {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        LOGGER.debug("Took lock for fetching game creation request status");
        try {
            if (gameCreationStatusMap.containsKey(sessionId))
                return gameCreationStatusMap.get(sessionId);
            throw new ResourceNotFoundException("Request with session id: " + sessionId + " not found");
        } finally {
            LOGGER.debug("Unlocked after fetching game creation request status");
            lock.unlock();
        }
    }

    public Map<String, MoveDetails> getMovesAndGameCountForFen(String fen) throws ScriptChessException {
        try {
            return chessService.getMovesForPosition(fen);
        } catch (ChessDbException e) {
            throw new ScriptChessException(e);
        }
    }

    public String createGameCreationRequestWithPreparedGames(byte[] bytes, String sourceFileName) {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        if(gameCreationStatusMap.size() > 0) {
            for(Map.Entry<String, GameCreationStatusClientModel> entry : gameCreationStatusMap.entrySet()) {
                if(!entry.getValue().isCompleted()) {
                    lock.unlock();
                    throw new IllegalStateException("Last Game creation process is still running");
                }
            }
        }
        lock.unlock();

        String session = Strings.generateUniqueSessionId();
        gameCreationStatusMap.put(session, new GameCreationStatusClientModel());
        gameCreationStatusMap.get(session).setSession(session);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    gameCreationStatusMap.get(session).setStartedAt(new Date().getTime());
                    gameCreationStatusMap.get(session).setSourceFileName(sourceFileName);
                    saveGameInBulkAtOnceWithPreparedGames(bytes, session);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    Lock lock = readWriteLock.writeLock();
                    lock.lock();
                    final GameCreationStatusClientModel gameCreationStatus = gameCreationStatusMap.get(session);
                    gameCreationStatus.setFailed(true);
                    gameCreationStatus.setError(e);
                    gameCreationStatus.setCompleted(true);
                    gameCreationStatusMap.get(session).setEndsAt(new Date().getTime());
                    lock.unlock();
                }
            }
        }).start();
        return session;
    }

    private List<MiniGameModel> saveGameInBulkAtOnceWithPreparedGames(byte[] bytes, String session) throws ScriptChessException {
        long startDesr = System.currentTimeMillis();
        List<Game> games = JsonUtil.getList(new String(bytes), Game.class);
        long endDesr = System.currentTimeMillis();
        LOGGER.info("Deserializing payload took " + DateUtil.getTimeDiff(endDesr , startDesr));
        return createAllGames(session, games);
    }
}