package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.DriverFactory;
import com.zylex.myscoreparser.Saver;
import com.zylex.myscoreparser.exceptions.ParseProcessorException;
import com.zylex.myscoreparser.model.Record;
import com.zylex.myscoreparser.model.RecordsLink;
import com.zylex.myscoreparser.repository.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ParseProcessor {

    private String[] countryLeagues;

    private ExecutorService service = Executors.newFixedThreadPool(DriverFactory.THREADS);

    public ParseProcessor(Repository repository) {
        this.countryLeagues = repository.getCountryLeagues();
    }

    public void process() {
        try {
            List<String> archiveLinks = processArchiveLinks();
            List<RecordsLink> recordsLinks = processRecordsLinks(archiveLinks);
            List<Record> records = processCoefficients(recordsLinks);
            Saver saver = new Saver();
            saver.processSaving(records);
        } catch (InterruptedException | ExecutionException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        } finally {
            service.shutdown();
            DriverFactory.quitDrivers();
        }
    }

    private List<String> processArchiveLinks() throws InterruptedException, ExecutionException {
        List<CallableArchiveParser> callableArchiveParsers = new ArrayList<>();
        for (String countryLeague : countryLeagues) {
            callableArchiveParsers.add(new CallableArchiveParser(countryLeague));
        }
        List<Future<List<String>>> futureArchiveParsers = service.invokeAll(callableArchiveParsers);
        return convertFutureArchiveLinks(futureArchiveParsers);
    }

    private List<String> convertFutureArchiveLinks(List<Future<List<String>>> futureArchiveParsers) throws InterruptedException, ExecutionException {
        List<String> archiveLinks = new ArrayList<>();
        for (Future<List<String>> future : futureArchiveParsers) {
            archiveLinks.addAll(future.get());
        }
        return archiveLinks;
    }

    private List<RecordsLink> processRecordsLinks(List<String> archiveLinks) throws InterruptedException, ExecutionException {
        List<CallableLeagueParser> callableLeagueParsers = new ArrayList<>();
        for (String link : archiveLinks) {
            callableLeagueParsers.add(new CallableLeagueParser(link));
        }
        List<Future<RecordsLink>> futureRecordsLinks = service.invokeAll(callableLeagueParsers);
        List<RecordsLink> recordsLinks = convertFutureLeagueLinks(futureRecordsLinks);
        Comparator<RecordsLink> sizeComparator = (o1, o2) -> Integer.compare(o2.getRecords().size(), o1.getRecords().size());
        Comparator<RecordsLink> seasonComparator = Comparator.comparingInt(this::getSeasonStartYear);
        return recordsLinks.stream()
                .sorted(sizeComparator.thenComparing(seasonComparator))
                .collect(Collectors.toList());
    }

    private List<RecordsLink> convertFutureLeagueLinks(List<Future<RecordsLink>> futureLeagueParsers) throws InterruptedException, ExecutionException {
        List<RecordsLink> recordsLinks = new ArrayList<>();
        for (Future<RecordsLink> futureLeagueResult: futureLeagueParsers) {
            RecordsLink newResult = futureLeagueResult.get();
            recordsLinks.add(newResult);
        }
        return recordsLinks;
    }

    private int getSeasonStartYear(RecordsLink result) {
        return Integer.parseInt(result.getRecords().get(0).getSeason().substring(0, 4));
    }

    private List<Record> processCoefficients(List<RecordsLink> recordsList) throws InterruptedException, ExecutionException {
        List<CallableCoefficientParser> callableCoefficientParsers = new ArrayList<>();
        for (RecordsLink records : recordsList) {
            callableCoefficientParsers.add(new CallableCoefficientParser(records.getRecords()));
        }
        List<Future<List<Record>>> futureRecordsLists = service.invokeAll(callableCoefficientParsers);
        return processRecords(futureRecordsLists);
    }

    private List<Record> processRecords(List<Future<List<Record>>> futureRecordsLists) throws InterruptedException, ExecutionException {
        List<Record> records = new ArrayList<>();
        for (Future<List<Record>> futureRecordsList : futureRecordsLists) {
            List<Record> recordList = futureRecordsList.get();
            records.addAll(recordList);
        }
        return records;
    }
}
