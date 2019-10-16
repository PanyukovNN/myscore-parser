package com.zylex.myscoreparser.tasks;

import com.zylex.myscoreparser.DriverFactory;
import com.zylex.myscoreparser.model.LeagueResult;
import org.openqa.selenium.WebDriver;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConnectionProcessor {

    private String[] countryLeagues;

    private ExecutorService service = Executors.newFixedThreadPool(8);

    public ConnectionProcessor(String[] countryLeagues) {
        this.countryLeagues = countryLeagues;
    }

    public void processParsers() throws InterruptedException, ExecutionException {
        try {
            List<String> archiveLinks = getArchiveLinks();
            List<LeagueResult> recordsList = getLeagueResults(archiveLinks);
            processRecordsParser(recordsList);
            service.shutdown();
        } finally {
            closeResources();
        }
    }

    private void processRecordsParser(List<LeagueResult> recordsList) throws InterruptedException {
        List<CallableParser> callableParserList = new ArrayList<>();
        for (LeagueResult records : recordsList) {
            callableParserList.add(new CallableParser(records.getLeagueLink(), records.getRecords()));
        }
        service.invokeAll(callableParserList);
    }

    private void closeResources() {
        for (WebDriver driver : DriverFactory.drivers) {
            driver.quit();
        }
    }

    private List<LeagueResult> getLeagueResults(List<String> archiveLinks) throws InterruptedException, ExecutionException {
        List<CallableRecordParser> callableRecordParserList = new ArrayList<>();
        for (String link : archiveLinks) {
            callableRecordParserList.add(new CallableRecordParser(link));
        }
        List<LeagueResult> recordsList = getRecordsList(service, callableRecordParserList);
        Comparator<LeagueResult> sizeComparator = (o1, o2) -> Integer.compare(o2.getRecords().size(), o1.getRecords().size());
        Comparator<LeagueResult> seasonComparator = Comparator.comparingInt(this::getSeasonStartYear);
        recordsList.sort(sizeComparator.thenComparing(seasonComparator));
        recordsList.forEach(list -> System.out.println(list.getRecords().size() + " " + list.getRecords().get(0).getSeason()));
        return recordsList;
    }

    private List<String> getArchiveLinks() throws InterruptedException, ExecutionException {
        List<CallableArchiveParser> callableArchiveParsers = new ArrayList<>();
        for (String countryLeague : countryLeagues) {
            callableArchiveParsers.add(new CallableArchiveParser(countryLeague));
        }
        List<Future<List<String>>> futureArchiveParsers = service.invokeAll(callableArchiveParsers);
        List<String> archiveLinks = new ArrayList<>();
        for (Future<List<String>> future : futureArchiveParsers) {
            archiveLinks.addAll(future.get());
        }
        return archiveLinks;
    }

    private int getSeasonStartYear(LeagueResult result) {
        return Integer.parseInt(result.getRecords().get(0).getSeason().substring(0, 4));
    }

    private List<LeagueResult> getRecordsList(ExecutorService service, List<CallableRecordParser> callableRecordParserList) throws InterruptedException, ExecutionException {
        List<Future<LeagueResult>> futureRecordLists = service.invokeAll(callableRecordParserList);
        List<LeagueResult> resultsList = new ArrayList<>();
        for (Future<LeagueResult> futureLeagueResult: futureRecordLists) {
            LeagueResult newResult = futureLeagueResult.get();
            resultsList.add(newResult);
        }
        return resultsList;
    }
}
