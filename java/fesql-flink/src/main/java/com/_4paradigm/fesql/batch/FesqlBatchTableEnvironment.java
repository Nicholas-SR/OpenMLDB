package com._4paradigm.fesql.batch;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.formats.parquet.ParquetTableSource;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FesqlBatchTableEnvironment {

    private Logger logger = LoggerFactory.getLogger(FesqlBatchTableEnvironment.class);

    private BatchTableEnvironment batchTableEnvironment;

    private Map<String, TableSchema> registeredTableSchemaMap = new HashMap<String, TableSchema>();

    public FesqlBatchTableEnvironment(BatchTableEnvironment batchTableEnvironment) {
        this.batchTableEnvironment = batchTableEnvironment;
    }

    BatchTableEnvironment getBatchTableEnvironment() {
        return this.batchTableEnvironment;
    }

    Map<String, TableSchema> getRegisteredTableSchemaMap() {
        return this.registeredTableSchemaMap;
    }

    void registerTableSource(String name, TableSource<?> tableSource) {
        this.batchTableEnvironment.registerTableSource(name, tableSource);

        // Register table name and schema
        if (this.registeredTableSchemaMap.containsKey(name)) {
            logger.warn(String.format("The table %s has been registered, ignore registeration", name));
        } else {
            this.registeredTableSchemaMap.put(name, tableSource.getTableSchema());
        }


    }

    void registerTableSink(String name, String[] fieldNames, TypeInformation<?>[] fieldTypes, TableSink<?> tableSink) {
        this.batchTableEnvironment.registerTableSink(name, fieldNames, fieldTypes, tableSink);
    }

    void registerTableSink(String name, TableSink<?> configuredSink) {
        this.batchTableEnvironment.registerTableSink(name, configuredSink);
    }

    FesqlTable sqlQuery(String query) {
        // Normalize SQL format
        if (!query.trim().endsWith(";")) {
            query = query.trim() + ";";
        }

        FesqlBatchPlanner planner = new FesqlBatchPlanner(this);

        try {
            return new FesqlTable(planner.plan(query));
        } catch (FeSQLException e) {
            e.printStackTrace();
            logger.error(String.format("Fail to run FESQL planner, error message: %s", e.getMessage()));
            return null;
        }

    }

    FesqlTable flinksqlQuery(String query) {
        return new FesqlTable(this.batchTableEnvironment.sqlQuery(query));
    }

    FesqlTable fromDataSet(DataSet<Row> dataSet) {
        return new FesqlTable(this.batchTableEnvironment.fromDataSet(dataSet));
    }

    FesqlTable fromDataSet(DataSet<Row> dataSet, String fields) {
        return new FesqlTable(this.batchTableEnvironment.fromDataSet(dataSet, fields));
    }

    FesqlTable fromDataSet(DataSet<Row> dataSet, Expression... fields) {
        return new FesqlTable(this.batchTableEnvironment.fromDataSet(dataSet, fields));
    }

}
