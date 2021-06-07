package com.mongo.migration.mongock.model;

import com.mongo.migration.mongock.enums.ZoneDirection;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document("metric")
public class Metric {

    private String id;
    private String zoneId;
    private ZoneDirection zoneDirection;
    private OffsetDateTime serviceTime;
    private OffsetDateTime openTime;
    private OffsetDateTime closeTime;
    private List<MetricsContainer> metricsContainers;
}

