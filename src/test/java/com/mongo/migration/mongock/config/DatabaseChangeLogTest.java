package com.mongo.migration.mongock.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongo.migration.mongock.enums.ZoneDirection;
import com.mongo.migration.mongock.model.Metric;
import com.mongo.migration.mongock.repository.MetricRepository;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class DatabaseChangeLogTest {

    public static final String DATABASE_NAME = "test";
    public static final String METRIC = "metric";
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private MetricRepository metricRepository;

    @Test
    void shouldCreateDefaultCollections() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
        MongoCursor<String> collectionsIterator = mongoDatabase.listCollectionNames()
                                                               .iterator();
        List<String> createdDatabases = new LinkedList<>();
        createdDatabases.add(METRIC);
        createdDatabases.add("container");
        while (collectionsIterator.hasNext()) {
            createdDatabases.remove(collectionsIterator.next());
        }

        assertTrue(createdDatabases.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("generatePartnerProducts")
    void seedMetricsShouldCreateDefaultMetrics(final String id, final String zoneId, final ZoneDirection direction) {

        final Optional<Metric> metricOptional = metricRepository.findByIdAndZoneId(id, zoneId);

        assertTrue(metricOptional.isPresent());
        assertEquals(direction, metricOptional.get()
                                              .getNewZoneDirection());
    }

    @Test
    void renameFieldShouldCreateNewZoneDirectionField() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> collection = mongoDatabase.getCollection(METRIC);
        final long count = collection.countDocuments(
            new BsonDocument("newZoneDirection", new BsonString(ZoneDirection.INBOUND.name())));

        assertEquals(997, count);
    }

    @Test
    void changeDataTypeFieldShouldMigrateToStringTheActiveField() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> collection = mongoDatabase.getCollection(METRIC);
        FindIterable<Document> metric = collection.find()
                                                 .limit(1);

        assertTrue(Objects.requireNonNull(metric.first())
                          .get("active") instanceof String);
    }

    private static Stream<Arguments> generatePartnerProducts() {
        final String zone1 = "Zone1";
        final String zone2 = "Zone2";
        return Stream.of(
            Arguments.of(
                "id-1",
                zone1,
                ZoneDirection.INBOUND
            ),
            Arguments.of(
                "id-2",
                zone2,
                ZoneDirection.OUTBOUND
            ),
            Arguments.of(
                "id-3",
                zone1,
                ZoneDirection.UNKNOWN
            )
        );
    }
}