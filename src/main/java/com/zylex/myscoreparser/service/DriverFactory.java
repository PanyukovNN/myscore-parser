package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.controller.LogType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    private int threads;

    private Queue<WebDriver> drivers = new ConcurrentLinkedQueue<>();

    public DriverFactory(int threads) {
        this.threads = threads;
        initiateDrivers();
    }

    private void initiateDrivers() {
        ConsoleLogger.startLogMessage(LogType.DRIVERS, threads);
        for (int i = 0; i < threads; i++) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1200,600");
            options.addArguments("--headless");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(600, TimeUnit.SECONDS);
            drivers.add(driver);
            ConsoleLogger.logDriver();
        }
    }

    int getThreads() {
        return threads;
    }

    synchronized WebDriver getDriver() throws InterruptedException {
        WebDriver driver = null;
        while (driver == null) {
            driver = drivers.poll();
            Thread.sleep(10);
        }
        return driver;
    }

    synchronized void addDriverToQueue(WebDriver driver) {
        drivers.add(driver);
    }

    public void quitDrivers() {
        for (WebDriver driver : drivers) {
            driver.quit();
        }
    }
}
