package com.example.multitenancylibrary.sql;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.ArrayList;
import java.util.List;

public class TablesFinder extends TablesNamesFinder {

    private final List<Table> tables = new ArrayList<>();

    @Override
    public void visit(Table tableName) {
        super.visit(tableName);
        this.tables.add(tableName);
    }

    public List<Table> getTables() {
        return this.tables;
    }
}
