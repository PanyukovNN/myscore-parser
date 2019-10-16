package com.zylex.myscoreparser.tasks;

import com.zylex.myscoreparser.DriverFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableArchiveParser implements Callable<List<String>> {

    private WebDriver driver = null;

    private String countryLeague;

    public CallableArchiveParser(String countryLeagues) {
        this.countryLeague = countryLeagues;
    }

    public List<String> call() throws InterruptedException {
        List<String> leagueLink;
        while (driver == null) {
            driver = DriverFactory.drivers.poll();
            Thread.sleep(10);
        }
        try {
            driver.navigate().to(String.format("https://www.myscore.ru/football/%s/archive/", countryLeague));
            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tournament-page-archiv")));
            leagueLink = parseArchive();
        } finally {
            DriverFactory.drivers.add(driver);
        }
        return leagueLink;
    }

    private List<String> parseArchive() {
        String pageSourse = driver.getPageSource();
        Document doc = Jsoup.parse(pageSourse);
        Elements archiveElements = doc.select("div.leagueTable__season > div.leagueTable__seasonName > a");
        List<String> archiveSeasons = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String seasonLink = archiveElements.get(i).attr("href").replace("/football/", "");
            archiveSeasons.add(seasonLink);
        }
        return archiveSeasons;
    }
}
