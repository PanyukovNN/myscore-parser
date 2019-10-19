package com.zylex.myscoreparser.repository;

import com.zylex.myscoreparser.exceptions.RepositoryException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Repository {

    private static Repository instance;

    private Repository() {
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public List<String> readLeaguesFromFile() {
        try {
            File file = new File(getClass().getClassLoader().getResource("leagues.txt").getFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> leagueLinks = new ArrayList<>();
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (!line.contains("//") && !line.isEmpty()) {
                    leagueLinks.add(line);
                }
            }
            return leagueLinks;
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }
}
