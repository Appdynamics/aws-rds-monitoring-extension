/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.rds;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.DimensionFilter;
import com.appdynamics.extensions.aws.config.IncludeMetric;
import com.appdynamics.extensions.aws.dto.AWSMetric;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Satish Muddam
 */
public class RDSMetricsProcessor implements MetricsProcessor {

    private static final String NAMESPACE = "AWS/RDS";

    private static final String DIMENSIONS = "DBInstanceIdentifier";


    private List<IncludeMetric> includeMetrics;

    private String rdsInstance;

    private List<String> includeDBIdentifiers;

    public RDSMetricsProcessor(List<IncludeMetric> includeMetrics, String rdsInstance, List<String> includeDBIdentifiers) {

        this.includeMetrics = includeMetrics;
        this.rdsInstance = rdsInstance;
        this.includeDBIdentifiers = includeDBIdentifiers;
    }

    public List<AWSMetric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName, LongAdder awsRequestsCounter) {

        List<DimensionFilter> dimensions = new ArrayList<DimensionFilter>();

        DimensionFilter dimensionFilter = new DimensionFilter();
        dimensionFilter.withName(DIMENSIONS);

        dimensions.add(dimensionFilter);

        DBIdentifiersMatcherPredicate predicate = new DBIdentifiersMatcherPredicate(includeDBIdentifiers);

        return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch, awsRequestsCounter,
                NAMESPACE,
                includeMetrics,
                dimensions,
                predicate);
    }

    public StatisticType getStatisticType(AWSMetric metric) {
        return MetricsProcessorHelper.getStatisticType(metric.getIncludeMetric(), includeMetrics);
    }

    public List<com.appdynamics.extensions.metrics.Metric> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
        Map<String, String> dimensionToMetricPathNameDictionary = new HashMap<String, String>();
        dimensionToMetricPathNameDictionary.put(DIMENSIONS, "DBInstance Identifier");

        return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats,
                dimensionToMetricPathNameDictionary, false);
    }

    public String getNamespace() {
        return NAMESPACE;
    }

}
