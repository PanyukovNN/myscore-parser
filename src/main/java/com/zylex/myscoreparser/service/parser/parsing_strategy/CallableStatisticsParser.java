package com.zylex.myscoreparser.service.parser.parsing_strategy;

import com.zylex.myscoreparser.controller.logger.BlockLogger;
import com.zylex.myscoreparser.exceptions.CoefficientParserException;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.model.StatisticsValue;
import com.zylex.myscoreparser.service.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CallableStatisticsParser implements Callable<List<Game>> {

    private WebDriver driver;

    private WebDriverWait wait;

    private List<Game> games;

    private int playOffGames = 0;

    private int noDataGames = 0;

    private DriverManager driverManager;

    private List<Game> archiveGames;

    private BlockLogger logger;

    public CallableStatisticsParser(BlockLogger logger, DriverManager driverManager, List<Game> archiveGames, List<Game> games) {
        this.logger = logger;
        this.driverManager = driverManager;
        this.archiveGames = archiveGames;
        this.games = games;
    }

    public List<Game> call() {
        try {
            driver = driverManager.getDriver();
            wait = new WebDriverWait(driver, 2);
            processStatisticParsing();
            return games;
        } catch (InterruptedException e) {
            throw new CoefficientParserException(e.getMessage(), e);
        } finally {
            driverManager.addDriverToQueue(driver);
            logger.setPlayOffGames(playOffGames);
            logger.setNoDataGames(noDataGames);
        }
    }

    private void processStatisticParsing() {
        for (Game game : games) {
            logger.logGame();
            driver.navigate().to(String.format("https://www.myscore.ru/match/%s/#match-statistics;0", game.getLink()));
            if (!statisticsTableExists()) {
                continue;
            }
            String pageSource = driver.getPageSource();
            Document document = Jsoup.parse(pageSource);
            if (isPlayOff(document)) {
                continue;
            }
            parseStatistics(game, document);
        }
    }

    private boolean statisticsTableExists() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("statBox")));
        } catch (TimeoutException | NoSuchElementException ignore) {
            noDataGames++;
            return false;
        }
        return true;
    }

    private boolean isPlayOff(Document document) {
        String league = document.select("span.description__country > a").text();
        if (league.contains("плей-офф")) {
            playOffGames++;
            return true;
        }
        return false;
    }

    private void parseStatistics(Game game, Document document) {
        Elements statisticsElements = document.select("div.statTextGroup");
        Map<String, StatisticsValue> statisticItems = game.getStatisticsItems();
        for (Element element : statisticsElements) {
            String homeValue = element.select("div.statText--homeValue").get(0).text();
            String titleValue = element.select("div.statText--titleValue").get(0).text();
            String awayValue = element.select("div.statText--awayValue").get(0).text();
            StatisticsValue statisticsValue = new StatisticsValue(homeValue, awayValue);
            statisticItems.put(titleValue, statisticsValue);
        }
        if (!statisticItems.isEmpty()) {
            archiveGames.add(game);
        }
    }
}
