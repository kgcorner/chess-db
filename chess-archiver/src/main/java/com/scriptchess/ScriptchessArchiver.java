package com.scriptchess;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scriptchess.exception.NetworkException;
import com.scriptchess.model.GameCreationStatus;
import com.scriptchess.util.DateUtil;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.SerializerDeserializerUtil;
import com.scriptchess.util.Strings;
import com.scriptchess.utility.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */

public class ScriptchessArchiver {

    private static final String NODE_JS_PATH = "/home/kumar/.nvm/versions/node/v15.5.1/bin/node"; //Path of Node js
    public static void main(String[] args) throws NetworkException {
        if(args.length != 1) {
            System.out.println("Usage:");
            System.out.println("java -jar <jar name> <path of dirs containing pgn files>");
            System.exit(-1);
        }
        String serPath = "/work/thechessnews/archivedPgns.ser"; // A serialized file that contains md5 of PGNs that have been archived successfully
        List<String> pgns = null;
        try {
            if(!new File(serPath).exists()) {
                pgns = new ArrayList<>();
                SerializerDeserializerUtil.serialize(pgns, serPath);

            } else {
                Object o = SerializerDeserializerUtil.deSerialize(serPath);
                if (o != null)
                    pgns = (List) o;
                else
                    pgns = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        String dirPath = args[0];
        File dir = new File(dirPath);
        if(!dir.isDirectory()) {
            System.out.println("Enter path to directory containing pgn files");
        }
        File[] pgnFiles = dir.listFiles();
        Request request = Request.getInstance();
        int count = 0;
        for(File file : pgnFiles) {
            if(!file.getName().toLowerCase().endsWith("pgn") && !file.getName().toLowerCase().endsWith("pgn.1")) {
                System.out.println("Skipping " + file.getName()+" as it doesn't has pgn extension");
                continue;
            }

            long start = System.currentTimeMillis();
            try {
                String data = FileUtil.readFile(file.getAbsolutePath());
                String md5 = Strings.getMd5(data);
                if(pgns.contains(md5)) {
                    System.out.println("Already created games from " + file.getAbsolutePath());
                    continue;
                }

                System.out.println("Creating games from " + file.getAbsolutePath());
                String gameFile = null;
                try {
                    gameFile = new GameAndFenPreparer().getGames(file.getAbsolutePath(), NODE_JS_PATH);
                } catch (Exception x) {
                    x.printStackTrace();
                    continue;
                }

                data = FileUtil.readFile(gameFile);
                if(data.length() <10)
                    continue;
                Request.MultipartData imageData = new Request.MultipartData();
                imageData.setData(data.getBytes());
                imageData.setName(file.getName());
                Map<String, Object> param = new HashMap<>();
                Map<String, String> headers  = new HashMap<>();
                param.put("image", imageData);
                System.out.println("Sending Request with " + file.getName());
                String session = request.doMultipartPostRequest("http://localhost:8083/games/game-creation-session", headers, param);
                GameCreationStatus status = waitForCompletion(session);
                if(status.getError() != null) {
                    System.out.println("Game batch " + file.getName() + "failed");
                    System.out.println(status.getError().getMessage());
                } else {
                    //request.doMultipartPostRequest("http://localhost:8083/manage/games/bulk", headers, param);
                    long end = System.currentTimeMillis();
                    SerializerDeserializerUtil.serialize(pgns, serPath);
                    count += status.getTotalCreatedGames();
                    System.out.println("Archived " + status.getTotalCreatedGames() + " games in " + DateUtil.getTimeDiff(status.getEndsAt() , status.getStartedAt()));
                    System.out.println("Total Created games: " + count);
                    pgns.add(md5);
                    if((status.getEndsAt() - status.getStartedAt()) < 60 *1000) {
                        try {
                            Thread.sleep(60* (long)1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                long end = System.currentTimeMillis();
                System.out.println("Failed after " +  (end-start) + "ms");
                System.exit(-1);
            } catch (NetworkException e) {
                System.out.println(e.getMessage());
                long end = System.currentTimeMillis();
                System.out.println("Failed after " +  (end-start) + "ms");
                System.exit(-1);
            }
        }
    }

    private static GameCreationStatus waitForCompletion(String session) {
        String url = "http://localhost:8083/games/create/" + session;
        Request instance = Request.getInstance();
        Map<String, String> headers  = new HashMap<>();
        while (true) {
            try {
                String response = instance.doGet(url, headers);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
                GameCreationStatus gameCreationStatus = gson.fromJson(response, GameCreationStatus.class);
                if(gameCreationStatus.isCompleted() || gameCreationStatus.getError() != null) {
                    return gameCreationStatus;
                } else {
                    Thread.sleep(10000);
                }
            } catch (NetworkException | InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}