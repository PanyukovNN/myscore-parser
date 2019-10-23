package com.zylex.myscoreparser;

import com.zylex.myscoreparser.controller.Saver;
import com.zylex.myscoreparser.repository.Repository;
import com.zylex.myscoreparser.service.DriverFactory;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Context {

    private DriverFactory driverFactory;

    private Saver saver;

    private Repository repository;


    public static Context instance;

    private Context() {
    }

    public static Context getInstance() {
        if (instance == null) {
            instance = new Context();
        }
        return instance;
    }

    public void init() {
        setSystemProperties();
        repository = Repository.getInstance();
        saver = new Saver();
    }

    private void setSystemProperties() {
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        loggers.forEach(logger -> logger.setLevel(org.apache.log4j.Level.OFF));
        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().version("77.0.3865.40").setup();
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
    }

    public DriverFactory getDriverFactory() {
        return driverFactory;
    }

    public Saver getSaver() {
        return saver;
    }

    public Repository getRepository() {
        return repository;
    }
}
