package com.mongo.migration.mongock.config;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongo.migration.mongock.enums.ZoneDirection;
import com.mongo.migration.mongock.model.Metric;
import com.mongo.migration.mongock.model.MetricsContainer;
import com.mongo.migration.mongock.repository.MetricRepository;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ChangeLog
public class DatabaseChangeLog {

    @ChangeSet(order = "001", id = "seedMetrics", author = "Frank", systemVersion = "1")
    public void seedMetrics(final MetricRepository expenseRepository) {
        final String id1 = "id-1";
        final String id2 = "id-2";
        final String id3 = "id-3";
        final String zone1 = "Zone1";
        final String zone2 = "Zone2";
        final List<Metric> metrics = Arrays.asList(
            createNewMetric(id1, zone1, ZoneDirection.INBOUND),
            createNewMetric(id2, zone2, ZoneDirection.OUTBOUND),
            createNewMetric(id3, zone1, ZoneDirection.UNKNOWN)
        );

        expenseRepository.insert(metrics);
    }

    @ChangeSet(order = "002", id = "seedCollections", author = "frank", systemVersion = "2")
    public void createCollection(final MongoDatabase mongoDatabase) {
        mongoDatabase.createCollection("container");
    }

    private Metric createNewMetric(final String id, final String zoneId, final ZoneDirection zoneDirection) {
        MetricsContainer metricsContainer = MetricsContainer.builder()
                                                            .position(0)
                                                            .contType("YYT:59:01")
                                                            .totalVolume(10.0)
                                                            .build();
        List<MetricsContainer> metricsContainers = Collections.singletonList(metricsContainer);
        return Metric.builder()
                     .id(id)
                     .zoneId(zoneId)
                     .zoneDirection(zoneDirection)
                     .metricsContainers(metricsContainers)
                     .build();
    }
}
