package com.zylex.myscoreparser.model;

import java.util.List;

public class RecordsLink {

    private String link;

    private List<Record> records;

    public RecordsLink(String link, List<Record> records) {
        this.link = link;
        this.records = records;
    }

    public String getLink() {
        return link;
    }

    public List<Record> getRecords() {
        return records;
    }
}
