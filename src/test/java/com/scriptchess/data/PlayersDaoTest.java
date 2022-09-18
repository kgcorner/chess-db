package com.scriptchess.data;

import com.scriptchess.exceptions.DAOException;
import com.scriptchess.models.Player;
import com.scriptchess.util.DirectoryUtil;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.SerializerDeserializerUtil;
import com.scriptchess.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 18/09/22
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SerializerDeserializerUtil.class, DirectoryUtil.class, FileUtil.class})
public class PlayersDaoTest {

    private String playersFilePath = ".players";
    private PlayersDao playersDao;
    private String sampleFideid = "sampleFideId";

    @Before
    public void setUp() throws Exception {
        playersDao = new PlayersDao();
        Whitebox.setInternalState(playersDao, "playersFilePath", playersFilePath);
        mockStatic(SerializerDeserializerUtil.class);
        mockStatic(DirectoryUtil.class);
        mockStatic(FileUtil.class);
    }

    @Test
    public void savePlayer() throws IOException, ClassNotFoundException, DAOException {
        String name = "Kumar Gaurav";
        Player player = new Player();
        player.setName(name);
        player.setFideId(sampleFideid);
        Map<String, Player> playersMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String randomId = TestUtils.generateString(5);
            Player p = new Player();
            p.setFideId(randomId);
            playersMap.put(randomId, p);
        }
        when(FileUtil.fileExists(playersFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenReturn(playersMap);
        Player result = playersDao.savePlayer(player);
        assertEquals(player, result);
        assertTrue("Player not found with fide id:" + player.getFideId(), playersMap.containsKey(player.getFideId()));
    }

    @Test
    public void savePlayerWithoutFideId() {
        try {
            playersDao.savePlayer(new Player());
        } catch (DAOException e) {
            assertEquals("Player without Fide id is not allowed", e.getMessage());
        }
    }

    @Test
    public void savePlayerWhenSerialaztionFails() throws IOException, ClassNotFoundException {
        try {
            String name = "Kumar Gaurav";
            Player player = new Player();
            player.setName(name);
            player.setFideId(sampleFideid);
            Map<String, Player> playersMap = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                String randomId = TestUtils.generateString(5);
                Player p = new Player();
                p.setFideId(randomId);
                playersMap.put(randomId, p);
            }
            when(FileUtil.fileExists(playersFilePath)).thenReturn(true);
            when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenReturn(playersMap);
            doThrow(new IOException()).when(SerializerDeserializerUtil.class);
            SerializerDeserializerUtil.serialize(playersMap, playersFilePath);
            playersDao.savePlayer(player);
        } catch (DAOException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void saveFirstPlayer() throws IOException, ClassNotFoundException, DAOException {
        String name = "Kumar Gaurav";
        Player player = new Player();
        player.setName(name);
        player.setFideId(sampleFideid);
        Map<String, Player> playersMap = new HashMap<>();
        when(FileUtil.fileExists(playersFilePath)).thenReturn(false);
        when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenReturn(playersMap);
        Player result = playersDao.savePlayer(player);
        assertEquals(player, result);
        assertTrue("Player not found with fide id:" + player.getFideId(), playersMap.containsKey(player.getFideId()));
    }

    @Test
    public void getPlayers()  throws IOException, ClassNotFoundException, DAOException  {
        Map<String, Player> playersMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String randomId = TestUtils.generateString(5);
            Player p = new Player();
            p.setFideId(randomId);
            playersMap.put(randomId, p);
        }
        when(FileUtil.fileExists(playersFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenReturn(playersMap);
        List<Player> result = playersDao.getPlayers();
        for(Player p : result) {
            assertTrue("Player not found with fide id:" + p.getFideId(), playersMap.containsKey(p.getFideId()) );
        }
    }

    @Test
    public void deletePlayer()  throws IOException, ClassNotFoundException, DAOException {
        Map<String, Player> playersMap = new HashMap<>();
        String randomId = "";
        for (int i = 0; i < 10; i++) {
            randomId = TestUtils.generateString(5);
            Player p = new Player();
            p.setFideId(randomId);
            playersMap.put(randomId, p);

        }
        when(FileUtil.fileExists(playersFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenReturn(playersMap);
        playersDao.deletePlayer(randomId);
        assertFalse("Player found with fide id:" + randomId,playersMap.containsKey(randomId));
    }

    @Test
    public void updatePlayerWihtoutFideId()  throws IOException, ClassNotFoundException, DAOException {
        try {
            playersDao.updatePlayer(new Player(), "someId");
        } catch (DAOException e) {
            assertEquals("Player without Fide id is not allowed", e.getMessage());
        }

        try {
            Player p =new Player();
            p.setFideId("some id");
            playersDao.updatePlayer(p, "");
        } catch (DAOException e) {
            assertEquals("Player without Fide id is not allowed", e.getMessage());
        }
    }
    @Test
    public void updatePlayer()  throws IOException, ClassNotFoundException, DAOException {
        Map<String, Player> playersMap = new HashMap<>();
        String randomId = "";
        for (int i = 0; i < 10; i++) {
            randomId = TestUtils.generateString(5);
            Player p = new Player();
            p.setFideId(randomId);
            playersMap.put(randomId, p);
        }
        when(FileUtil.fileExists(playersFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenReturn(playersMap);
        String newId = TestUtils.generateString(5);
        Player playerToUpdate = playersMap.get(randomId);
        playerToUpdate.setFideId(newId);
        playersDao.updatePlayer(playerToUpdate, randomId);
        assertFalse("Player found with fide id:" + randomId, playersMap.containsKey(randomId));
        assertTrue("Player not found with fide id:" + newId,playersMap.containsKey(newId));
    }

    @Test
    public void getPlayer()  throws IOException, ClassNotFoundException, DAOException  {
        Map<String, Player> playersMap = new HashMap<>();
        String randomId = "";
        for (int i = 0; i < 10; i++) {
            randomId = TestUtils.generateString(5);
            Player p = new Player();
            p.setFideId(randomId);
            playersMap.put(randomId, p);
        }
        when(FileUtil.fileExists(playersFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenReturn(playersMap);
        Player expectedPlayer = playersMap.get(randomId);
        Player result = playersDao.getPlayer(randomId);
        assertEquals(expectedPlayer, result);
    }

    @Test
    public void getPlayerSerializationFails()  throws IOException, ClassNotFoundException  {
        when(FileUtil.fileExists(playersFilePath)).thenReturn(false);
        doThrow(new IOException()).when(SerializerDeserializerUtil.class);
        SerializerDeserializerUtil.serialize(new HashMap<>(), playersFilePath);
        try {
            playersDao.getPlayer("randomId");
        } catch (DAOException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void getPlayerDeSerializationFails()  throws IOException, ClassNotFoundException  {
        Map<String, Player> playersMap = new HashMap<>();
        String randomId = "";
        for (int i = 0; i < 10; i++) {
            randomId = TestUtils.generateString(5);
            Player p = new Player();
            p.setFideId(randomId);
            playersMap.put(randomId, p);
        }
        when(FileUtil.fileExists(playersFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenThrow(new IOException());
        try {
            playersDao.getPlayer("randomId");
        } catch (DAOException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void getPlayerThatDoesNotExists()  throws IOException, ClassNotFoundException, DAOException  {
        Map<String, Player> playersMap = new HashMap<>();
        String randomId = "";
        for (int i = 0; i < 10; i++) {
            randomId = TestUtils.generateString(5);
            Player p = new Player();
            p.setFideId(randomId);
            playersMap.put(randomId, p);
        }
        when(FileUtil.fileExists(playersFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(playersFilePath)).thenReturn(playersMap);
        Player result = playersDao.getPlayer("Some other id");
        assertNull(result);
    }

    @Test
    public void getPlayerWithEmptyFideId()  throws IOException, ClassNotFoundException, DAOException  {
        try {
            playersDao.getPlayer("");
        } catch (DAOException e) {
            assertEquals("Player without Fide id is not allowed", e.getMessage());
        }
    }
}