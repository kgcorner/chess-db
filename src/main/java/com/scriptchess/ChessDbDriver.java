package com.scriptchess;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 16/09/22
 */

@SpringBootApplication
public class ChessDbDriver {
    public static void init(String[] args) {
        SpringApplication.run(ChessDbDriver.class, args);
    }
}