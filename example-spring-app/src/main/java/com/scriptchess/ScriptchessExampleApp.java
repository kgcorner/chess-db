package com.scriptchess;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/05/23
 */
@SpringBootApplication
public class ScriptchessExampleApp {
    public static void main(String[] args) {
        System.out.println("Project base directory: ");
        System.out.println(new File("").getAbsolutePath());
        SpringApplication.run(ScriptchessExampleApp.class, args);
    }
}