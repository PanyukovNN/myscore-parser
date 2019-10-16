package com.zylex.myscoreparser.model;

import java.util.List;

public class LeagueResult {

    private String leagueLink;

    private List<Record> records;

    public LeagueResult(String leagueLink, List<Record> records) {
        this.leagueLink = leagueLink;
        this.records = records;
    }

    public String getLeagueLink() {
        return leagueLink;
    }

    public List<Record> getRecords() {
        return records;
    }
}
