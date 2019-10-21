package com.zylex.myscoreparser.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    public static int THREADS = 8;

    public static Queue<WebDriver> drivers = new ConcurrentLinkedQueue<>();

    static {
        System.setProperty("webdriver.chrome.driver", "C:\\java\\external-files\\chromedriver.exe");
        for (int i = 0; i < THREADS; i++) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1200,600");
            options.addArguments("--headless");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(1000, TimeUnit.SECONDS);
            drivers.add(driver) ;
            System.out.println((i + 1) + " chrome driver is started.");
        }
        System.out.println("All drivers started successfully.");
    }

    public static void quitDrivers() {
        for (WebDriver driver : drivers) {
            driver.quit();
        }
    }
}
