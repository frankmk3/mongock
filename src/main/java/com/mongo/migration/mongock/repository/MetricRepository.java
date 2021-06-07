package com.mongo.migration.mongock.repository;

import com.mongo.migration.mongock.model.Metric;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MetricRepository extends MongoRepository<Metric, String> {

    Optional<Metric> findByIdAndZoneId(String id, String zoneId);
}
