package com.zylex.myscoreparser.service.parser;

import com.zylex.myscoreparser.controller.logger.BlockLogger;
import com.zylex.myscoreparser.exceptions.LeagueParserException;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.controller.GameRepository;
import com.zylex.myscoreparser.service.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableSeasonParser implements Callable<List<Game>> {

    private String leagueLink;

    private WebDriver driver = null;

    private WebDriverWait wait;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private DriverManager driverManager;

    private GameRepository gameRepository;

    private BlockLogger logger;

    CallableSeasonParser(BlockLogger logger, DriverManager driverManager, GameRepository gameRepository, String leagueLink) {
        this.logger = logger;
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
        this.leagueLink = leagueLink;

    }

    public List<Game> call() {
        try {
            driver = driverManager.getDriver();
            wait = new WebDriverWait(driver, 10);
            return processLeagueParsing(leagueLink);
        } catch (InterruptedException e) {
            throw new LeagueParserException(e.getMessage(), e);
        } finally {
            driverManager.addDriverToQueue(driver);
        }
    }

    private List<Game> processLeagueParsing(String leagueLink) {
        driver.navigate().to(String.format("https://www.myscore.ru/football/%sresults/", leagueLink));
        showMore();
        return parseGames(driver);
    }

    private List<Game> parseGames(WebDriver driver) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.elementToBeClickable(By.className("event__match")));
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);
        List<Game> games = new ArrayList<>();
        Elements gamesElements = document.select("div.event__match");
        String country = document.select("span.event__title--type").first().text();
        String league = document.select("div.teamHeader__name").first().text();
        String season = document.select("div.teamHeader__text").first().text().replace("/", "");
        int seasonStartMonth = findSeasonStartMonth(gamesElements.last());
        logger.logSeason(gamesElements.size());
        int existInArchive = 0;
        for (Element gameElement : gamesElements) {
            String seasonStartYear = season.substring(0, 4);
            LocalDateTime gameDateTime = processDate(seasonStartYear, seasonStartMonth, gameElement);
            String firstCommand = gameElement.select("div.event__participant--home").text();
            String secondCommand = gameElement.select("div.event__participant--away").text();
            String[] scores = gameElement.select("div.event__scores > span").text().split(" ");
            int firstBalls = Integer.parseInt(scores[0]);
            int secondBalls = Integer.parseInt(scores[1]);
            String link = gameElement.id().replace("g_1_", "");
            Game game = new Game(country, league, season, gameDateTime, firstCommand, secondCommand, firstBalls, secondBalls, link);
            if (gameRepository.getArchiveGames().contains(game)) {
                existInArchive++;
                continue;
            }
            games.add(game);
        }
        logger.addBlockGamesArchiveExist(existInArchive);
        return games;
    }

    private void showMore() {
        while (true) {
            try {
                Thread.sleep(1000);
                driver.findElement(By.className("event__more")).click();
            } catch (NoSuchElementException e) {
                break;
            } catch (StaleElementReferenceException | ElementClickInterceptedException | InterruptedException ignore) {
            }
        }
    }

    private int findSeasonStartMonth(Element lastGame) {
        String time = lastGame.select("div.event__time").text();
        return Integer.parseInt(time.substring(3, 5));
    }

    private LocalDateTime processDate(String startYear, int seasonStartMonth, Element element) {
        String time = element.select("div.event__time").text();
        time = time.replace(". ", "." + startYear + " ").substring(0, 16);
        LocalDateTime dateTime = LocalDateTime.parse(time, FORMATTER);
        int month = dateTime.getMonth().getValue();
        if (month < seasonStartMonth) {
            dateTime = dateTime.plusYears(1);
        }
        return dateTime;
    }
}
