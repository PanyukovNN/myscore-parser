package com.zylex.myscoreparser.repository;

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

    private String[] countryLeagues = {
            "austria/tipico-bundesliga",
            "australia/a-league",
//            "england/premier-league",
//            "england/championship",
//            "argentina/superliga",
//            "belarus/vysshaya-liga",
//            "belgium/jupiler-league",
//            "bulgaria/parva-liga",
//            "brazil/serie-a",
//            "brazil/serie-b",
    };

    public String[] getCountryLeagues() {
        return countryLeagues;
    }
}
