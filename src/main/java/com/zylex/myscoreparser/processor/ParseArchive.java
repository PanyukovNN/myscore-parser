package com.zylex.myscoreparser.processor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseArchive {

    public static Map<String, List<String>> processArchive(String[] countryLeagues) {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Map<String, List<String>> leagueHrefs = new HashMap<>();
        for (String countryLeague : countryLeagues) {
            leagueHrefs.putIfAbsent(countryLeague, new ArrayList<>());
            driver.navigate().to(String.format("https://www.myscore.ru/football/%s/archive/", countryLeague));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tournament-page-archiv")));
            String pageSourse = driver.getPageSource();
            Document doc = Jsoup.parse(pageSourse);
            Elements archiveElements = doc.select("div.leagueTable__season > div.leagueTable__seasonName > a");
            int i = 0;
            for (Element element : archiveElements) {
                String href = element.attr("href").replace("/football/", "");
                leagueHrefs.get(countryLeague).add(href);
                if (i++ == 3) {
                    break;
                }
            }
        }
        driver.close();
        return leagueHrefs;
    }
}
