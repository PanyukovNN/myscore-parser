package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DriverFactory {

    public static int THREADS = 8;

    public static Queue<WebDriver> drivers = new ConcurrentLinkedQueue<>();

    static {
        BasicConfigurator.configure();
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            logger.setLevel(org.apache.log4j.Level.OFF);
        }
        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().version("77.0.3865.40").setup();
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        ConsoleLogger.writeInLine("Starting chrome drivers");
        for (int i = 0; i < THREADS; i++) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1200,600");
            options.addArguments("--headless");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(600, TimeUnit.SECONDS);
            drivers.add(driver);
            ConsoleLogger.writeInLine(".");
        }
        ConsoleLogger.writeLine("\nAll drivers started successfully.");
    }

    public static void quitDrivers() {
        for (WebDriver driver : drivers) {
            driver.quit();
        }
    }
}
