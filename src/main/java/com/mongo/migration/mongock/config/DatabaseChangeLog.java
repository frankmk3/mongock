package com.mongo.migration.mongock.config;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongo.migration.mongock.enums.ZoneDirection;
import com.mongo.migration.mongock.model.Metric;
import com.mongo.migration.mongock.model.MetricsContainer;
import com.mongo.migration.mongock.repository.MetricRepository;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

@ChangeLog
public class DatabaseChangeLog {

    public static final String METRIC_COLLECTION = "metric";
    public static final String CONTAINER_COLLECTION = "container";
    public static final String ACTIVE_FIELD = "active";

    @ChangeSet(order = "001", id = "seedMetrics", author = "Frank", systemVersion = "1.4")
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

        List<Metric> metricsList = IntStream.range(4, 1000)
                                            .mapToObj(
                                                id -> createNewMetric("id-" + id, "Zone-" + id, ZoneDirection.INBOUND))
                                            .collect(Collectors.toList());
        expenseRepository.insert(metricsList);
    }

    @ChangeSet(order = "002", id = "seedCollections", author = "frank", systemVersion = "1.5")
    public void createCollection(final MongoDatabase mongoDatabase) {
        mongoDatabase.createCollection(CONTAINER_COLLECTION);
    }


    @ChangeSet(order = "003", id = "renameField", author = "frank", systemVersion = "1.5")
    public void renameField(final MongoDatabase mongoDatabase) {
        mongoDatabase.createCollection("container3");

        MongoCollection<Document> container = mongoDatabase.getCollection(METRIC_COLLECTION);
        container.updateMany(new BsonDocument(), new BsonDocument("$rename",
            new BsonDocument("zoneDirection",
                new BsonString("newZoneDirection")
            )));
    }


    @ChangeSet(order = "004", id = "addField", author = "frank", systemVersion = "1.5")
    public void addField(final MongoDatabase mongoDatabase) {
        MongoCollection<Document> container = mongoDatabase.getCollection(METRIC_COLLECTION);
        container.updateMany(new BsonDocument(), new BsonDocument("$set",
            new BsonDocument(ACTIVE_FIELD,
                new BsonBoolean(true)
            )));
    }


    @ChangeSet(order = "005", id = "deleteField", author = "frank", systemVersion = "1.5")
    public void deleteField(final MongoDatabase mongoDatabase) {
        MongoCollection<Document> container = mongoDatabase.getCollection(METRIC_COLLECTION);
        container.updateMany(new BsonDocument(), new BsonDocument("$unset",
            new BsonDocument("metricsContainers.$[].totalVolume",
                new BsonInt32(1)
            )));
    }

    @ChangeSet(order = "006", id = "changeDataType", author = "frank", systemVersion = "1.5")
    public void changeDataType(final MongoDatabase mongoDatabase) {
        MongoCollection<Document> container = mongoDatabase.getCollection(METRIC_COLLECTION);
        long count = container.countDocuments();
        int pageSize = 20;
        int page = 0;
        for (; pageSize * page < count; page++) {
            FindIterable<Document> metrics = container.find()
                                                      .limit(pageSize)
                                                      .skip(page * pageSize);
            metrics.forEach(m -> {
                if (m.containsKey(ACTIVE_FIELD) && m.get(ACTIVE_FIELD) instanceof Boolean) {
                    Bson filter = new BsonDocument("_id", new BsonString((String) m.get("_id")));
                    container.updateOne(filter, new BsonDocument("$set",
                        new BsonDocument(ACTIVE_FIELD,
                            new BsonString(String.valueOf(m.get(ACTIVE_FIELD)))
                        )));
                }
            });
        }
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
