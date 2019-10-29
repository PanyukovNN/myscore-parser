package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.DriverManager;
import com.zylex.myscoreparser.service.ParseProcessor;

class Context {

    private Saver saver;

    private Repository repository;

    private ParseProcessor parseProcessor;

    private static Context instance;

    private Context() {
    }

    static Context getInstance() {
        if (instance == null) {
            instance = new Context();
        }
        return instance;
    }

    void init() {
        parseProcessor = new ParseProcessor();
        repository = new Repository();
        saver = new Saver();
    }

    DriverManager getDriverFactory(int threads) {
        return new DriverManager(threads);
    }

    Saver getSaver() {
        return saver;
    }

    Repository getRepository() {
        return repository;
    }

    ParseProcessor getParseProcessor() {
        return parseProcessor;
    }
}
