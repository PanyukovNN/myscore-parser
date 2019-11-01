package com.zylex.myscoreparser.repository;

import com.zylex.myscoreparser.exceptions.RepositoryException;
import com.zylex.myscoreparser.service.DriverManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LeagueRepository {

    public List<List<String>> readDiscreteLeaguesFromFile(int threads) {
        try {
            InputStream inputStream = DriverManager.class.getResourceAsStream("/leagues.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> leagueLinks = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("//") && !line.isEmpty()) {
                    leagueLinks.add(line);
                }
            }
            return getDiscreteLeagueList(threads, leagueLinks);
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    private List<List<String>> getDiscreteLeagueList(int threads, List<String> leagueLinks) {
        List<List<String>> discreteList = new ArrayList<>();
        while (true) {
            if (leagueLinks.size() <= threads) {
                discreteList.add(leagueLinks);
                break;
            }
            discreteList.add(leagueLinks.subList(0, threads));
            leagueLinks = leagueLinks.subList(threads, leagueLinks.size());
        }
        return discreteList;
    }
}
