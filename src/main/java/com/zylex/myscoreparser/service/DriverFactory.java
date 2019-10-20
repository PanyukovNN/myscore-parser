package com.zylex.myscoreparser.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    public static int THREADS = 4;

    public static Queue<WebDriver> drivers = new ConcurrentLinkedQueue<>();

    static {
        System.setProperty("webdriver.chrome.driver", "C:\\java\\external-files\\chromedriver.exe");
        for (int i = 0; i < THREADS; i++) {
            WebDriver driver = new ChromeDriver();
            driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
            drivers.add(driver);
        }
    }

    public static void quitDrivers() {
        for (WebDriver driver : drivers) {
            driver.quit();
        }
    }
}
