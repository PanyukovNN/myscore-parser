package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.DriverFactory;
import com.zylex.myscoreparser.service.ParseProcessor;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

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
        setSystemProperties();
        parseProcessor = new ParseProcessor();
        repository = new Repository();
        saver = new Saver();
    }

    @SuppressWarnings("unchecked")
    private void setSystemProperties() {
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        loggers.forEach(logger -> logger.setLevel(org.apache.log4j.Level.OFF));
        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().version("77.0.3865.40").setup();
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
    }

    DriverFactory getDriverFactory(int threads) {
        return new DriverFactory(threads);
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
