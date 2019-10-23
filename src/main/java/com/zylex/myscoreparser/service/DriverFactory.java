package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    public static int THREADS = 2;

    public static Queue<WebDriver> drivers = new ConcurrentLinkedQueue<>();

    static {
        ConsoleLogger.writeInLine("Starting chrome drivers: 0 out of " + THREADS);
        for (int i = 0; i < THREADS; i++) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1200,600");
            options.addArguments("--headless");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(600, TimeUnit.SECONDS);
            drivers.add(driver);
            String count = (i + 1) + " out of " + THREADS;
            ConsoleLogger.writeInLine(StringUtils.repeat("\b", count.length()) + count);
        }
        System.out.println();
    }

    public static synchronized WebDriver getDriver() throws InterruptedException {
        WebDriver driver = null;
        while (driver == null) {
            driver = DriverFactory.drivers.poll();
            Thread.sleep(10);
        }
        return driver;
    }

    public static void quitDrivers() {
        for (WebDriver driver : drivers) {
            driver.quit();
        }
    }
}
