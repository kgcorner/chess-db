package com.scriptchess.data;

import com.scriptchess.exceptions.DAOException;
import com.scriptchess.exceptions.DirectoryNotFound;
import com.scriptchess.exceptions.FileExists;
import com.scriptchess.exceptions.NotADirectoryException;
import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.util.DirectoryUtil;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.SerializerDeserializerUtil;
import com.scriptchess.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 17/09/22
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SerializerDeserializerUtil.class, DirectoryUtil.class, FileUtil.class})
public class MovesDaoTest {

    private String movesPath = ".moves";
    private static final String COUNT_FILE_NAME = ".count";
    private static final String GAME_INDEX_FILE_NAME = ".games";
    private MovesDao movesDao;

    @Before
    public void setUp() throws Exception {
        movesDao = new MovesDao();
        Whitebox.setInternalState(movesDao, "movesPath", movesPath);
        mockStatic(SerializerDeserializerUtil.class);
        mockStatic(DirectoryUtil.class);
        mockStatic(FileUtil.class);
    }

    @Test
    public void saveMovesOfEmptyGame() throws IOException, ClassNotFoundException, DAOException, FileExists {
        assertFalse(movesDao.saveMoves(new Game(), 10));
    }

    @Test
    public void saveMoves() throws IOException, ClassNotFoundException, DAOException, FileExists {
        int moveCount = 10;
        Game game = new Game();
        List<Move> moves = new ArrayList<>();
        int index = 10;
        for (int i = 0; i < moveCount; i++) {
            Move wMove = new Move(i, TestUtils.generateString(4), 0);
            Move bMove = new Move(i, TestUtils.generateString(4), 0);
            moves.add(wMove);
            moves.add(bMove);
        }
        game.setMoves(moves);
        StringBuilder sbMove = new StringBuilder();
        sbMove.append(movesPath);
        List<String> countFilePathToVerify = new ArrayList<>();
        List<String> indexFilePathToVerify = new ArrayList<>();
        Map<String, Integer> moveCountFilePathMap = new HashMap<>();
        Map<String, List<Integer>> indexFilePathMap = new HashMap<>();
        for(Move move : game.getMoves()) {
            String moveStr = move.getMove();
            sbMove.append(File.separator + moveStr);
            when(DirectoryUtil.createDirectory(sbMove.toString())).thenReturn(true);
            String countFilePath = sbMove.toString()+ File.separator + COUNT_FILE_NAME;
            String indexFilePath = sbMove.toString()+ File.separator + GAME_INDEX_FILE_NAME;
            countFilePathToVerify.add(countFilePath);
            indexFilePathToVerify.add(indexFilePath);
            int gameCountOnMove = TestUtils.generateInt(10);
            //Just to add test case of new game
            if(gameCountOnMove == 3)
                gameCountOnMove = 0;
            when(FileUtil.readFile(countFilePath)).thenReturn(gameCountOnMove+"");
            moveCountFilePathMap.put(countFilePath, gameCountOnMove + 1);
            List<Integer> existingIndex = new ArrayList<>();
            for (int i = 0; i < gameCountOnMove; i++) {
                existingIndex.add(TestUtils.generateInt(425));
            }
            List<Integer> newIndexes = new ArrayList<>();
            if(gameCountOnMove > 0) {
                newIndexes.addAll(existingIndex);
                newIndexes.add(index);
            }
            if(gameCountOnMove == 0){
                newIndexes.add(index);
            }
            indexFilePathMap.put(indexFilePath, newIndexes);
            if(gameCountOnMove > 0)
                when(FileUtil.fileExists(indexFilePath)).thenReturn(true);
            else
                when(FileUtil.fileExists(indexFilePath)).thenReturn(false);

            when(SerializerDeserializerUtil.deSerialize(indexFilePath)).thenReturn(existingIndex);
        }
        boolean result = movesDao.saveMoves(game, index);
        assertTrue(result);

        for(String path : countFilePathToVerify) {
            verifyStatic(FileUtil.class);
            FileUtil.createFile(path);
        }

        for(Map.Entry<String, Integer> entry : moveCountFilePathMap.entrySet()) {
            verifyStatic(FileUtil.class);
            FileUtil.writeData(entry.getValue().toString().getBytes(), true, entry.getKey());
        }
    }

    @Test
    public void getMovesAndGameCountAfter() throws NotADirectoryException, DirectoryNotFound, IOException, DAOException {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Move wMove = new Move(i, TestUtils.generateString(4), 0);
            Move bMove = new Move(i, TestUtils.generateString(4), 0);
            moves.add(wMove);
            moves.add(bMove);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(movesPath);
        for(Move move : moves) {
            String moveNotation = move.getMove();
            sb.append(File.separator + moveNotation);
        }
        String movesPath = sb.toString();
        List<String> nextExistingMoves = new ArrayList<>();
        for (int i = 10; i < 15; i++) {
            nextExistingMoves.add(TestUtils.generateString(4));
        }
        when(DirectoryUtil.getDirectories(movesPath)).thenReturn(nextExistingMoves);
        Map<String, Integer> moveGameCountMap = new HashMap<>();
        for(String m : nextExistingMoves) {
            int gameCount = TestUtils.generateInt(50);
            String cntPath = movesPath + File.separator + m +
                File.separator + COUNT_FILE_NAME;
            when(FileUtil.readFile(cntPath)).thenReturn(gameCount+"");
            moveGameCountMap.put(m, gameCount);
        }
        Map<String, Integer> result = movesDao.getMovesAndGameCountAfter(moves);
        assertEquals(moveGameCountMap, result);
    }
}