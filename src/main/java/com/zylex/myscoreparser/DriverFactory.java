package com.zylex.myscoreparser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DriverFactory {

    public static final int THREADS = 8;

    public static Queue<WebDriver> drivers = new ConcurrentLinkedQueue<>();

    static {
        for (int i = 0; i < THREADS; i++) {
            drivers.add(new ChromeDriver());
        }
    }

    public static void quitDrivers() {
        for (WebDriver driver : drivers) {
            driver.quit();
        }
    }
}
