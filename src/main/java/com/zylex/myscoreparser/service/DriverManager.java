package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.logger.DriverLogger;
import com.zylex.myscoreparser.controller.logger.LogType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DriverManager {

    private DriverLogger logger = new DriverLogger();

    private int threads;

    private Queue<WebDriver> drivers = new ConcurrentLinkedQueue<>();

    public DriverManager(int threads) {
        this.threads = threads;
    }

    public void initiateDrivers() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        WebDriverManager.chromedriver().version("77.0.3865.40").setup();
        logger.startLogMessage(LogType.DRIVERS, threads);
        for (int i = 0; i < threads; i++) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1200,600");
            options.addArguments("--headless");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(600, TimeUnit.SECONDS);
            drivers.add(driver);
            logger.logDriver();
        }
    }

    public int getThreads() {
        return threads;
    }

    public synchronized WebDriver getDriver() throws InterruptedException {
        WebDriver driver = null;
        while (driver == null) {
            driver = drivers.poll();
            Thread.sleep(10);
        }
        return driver;
    }

    public synchronized void addDriverToQueue(WebDriver driver) {
        drivers.add(driver);
    }

    public void quitDrivers() {
        for (WebDriver driver : drivers) {
            driver.quit();
        }
    }
}
