package com.zylex.myscoreparser;

import java.io.IOException;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException, ParseException {
        System.setProperty("webdriver.chrome.driver", "C:\\java\\projects\\myscoreparser\\chromedriver.exe");
        String country = "england";
        String league = "/premier-league";
        String[] leagueYears = {"-2016-2017", "-2017-2018", "-2018-2019", ""};
        ConnectionProcessor processor2016 = new ConnectionProcessor(country, league, leagueYears[0]);
        processor2016.start();
        ConnectionProcessor processor2017 = new ConnectionProcessor(country, league, leagueYears[1]);
        processor2017.start();
        ConnectionProcessor processor2018 = new ConnectionProcessor(country, league, leagueYears[2]);
        processor2018.start();
        ConnectionProcessor processor2019 = new ConnectionProcessor(country, league, leagueYears[3]);
        processor2019.start();

    }
}