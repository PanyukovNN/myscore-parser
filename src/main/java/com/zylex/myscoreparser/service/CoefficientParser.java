package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.Main;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Record;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;

public class CoefficientParser {

    private WebDriver driver;

    private WebDriverWait wait;

    private int damagedFiles = 0;

    public CoefficientParser(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 10);
    }

    public void processCoefficientParsing(List<Record> records) {
        int i = 1;
        for (Record record : records) {
            try {
                driver.navigate().to(String.format("https://www.myscore.ru/match/%s/#odds-comparison;1x2-odds;full-time", record.getCoefHref()));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("odds_1x2")));
                String pageSourse = driver.getPageSource();
                Document document = Jsoup.parse(pageSourse);
                parseCoefficients(record, document);
                System.out.println(i++ + ") " + record);
            } catch (TimeoutException e) {
                damagedFiles++;
                System.out.println(i++ + ") No coefficients: " + record);
            }
        }
        System.out.println("Records without coefficients: " + damagedFiles);
        Main.totalWithNoCoef.addAndGet(damagedFiles);
    }

    private void parseCoefficients(Record record, Document document) {
        Element coefTable = document.select("table#odds_1x2").first();
        Elements coefRecords = coefTable.select("tbody > tr");
        Map<String, Coefficient> coefficients = record.getCoefficients();
        for (Element element : coefRecords) {
            String bookmaker = element.select("td.bookmaker > div > a").first().attr("title");
            bookmaker = fixBookmakerName(bookmaker);
            if (checkBookmaker(bookmaker)) {
                String[] allCoef = element.select("td.kx > span").text().split(" ");
                Coefficient coefficient = new Coefficient(bookmaker, allCoef[0], allCoef[1], allCoef[2]);
                coefficients.putIfAbsent(bookmaker, coefficient);
            }
        }
    }

    private String fixBookmakerName(String bookmaker) {
        if (bookmaker.equalsIgnoreCase("1xStavka.ru")) {
            return "1XBET";
        } else if (bookmaker.equalsIgnoreCase("Winline.ru")) {
            return "Winline";
        } else if (bookmaker.equalsIgnoreCase("Leon.ru")) {
            return "Leon";
        }
        return "Wrong bookmaker name";
    }

    private boolean checkBookmaker(String bookmaker) {
        String[] bookmakers = {"1XBET", "Winline", "Leon"};
        for (String bm: bookmakers) {
            if (bm.equalsIgnoreCase(bookmaker)) {
                return true;
            }
        }
        return false;
    }
}
