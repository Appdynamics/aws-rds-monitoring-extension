/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.rds;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.rds.config.RDSConfiguration;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.appdynamics.extensions.aws.Constants.METRIC_PATH_SEPARATOR;

/**
 * @author Satish Muddam
 */
public class RDSMonitor extends SingleNamespaceCloudwatchMonitor<RDSConfiguration> {

    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(RDSMonitor.class);

    private static final String DEFAULT_METRIC_PREFIX = String.format("%s%s%s%s",
            "Custom Metrics", METRIC_PATH_SEPARATOR, "Amazon RDS", METRIC_PATH_SEPARATOR);

    public RDSMonitor() {
        super(RDSConfiguration.class);
    }

    @Override
    public String getDefaultMetricPrefix() {
        return DEFAULT_METRIC_PREFIX;
    }

    @Override
    public String getMonitorName() {
        return "RDSMonitor";
    }

    @Override
    protected List<Map<String, ?>> getServers() {
        return new ArrayList<Map<String, ?>>();
    }

    @Override
    protected void initialize(RDSConfiguration config) {
        super.initialize(config);
    }

    @Override
    protected NamespaceMetricStatisticsCollector getNamespaceMetricsCollector(
            RDSConfiguration config) {
        MetricsProcessor metricsProcessor = createMetricsProcessor(config);

        return new NamespaceMetricStatisticsCollector
                .Builder(config.getAccounts(),
                config.getConcurrencyConfig(),
                config.getMetricsConfig(),
                metricsProcessor,
                config.getMetricPrefix())
                .withCredentialsDecryptionConfig(config.getCredentialsDecryptionConfig())
                .withProxyConfig(config.getProxyConfig())
                .build();
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    private MetricsProcessor createMetricsProcessor(RDSConfiguration config) {
        return new RDSMetricsProcessor(
                config.getMetricsConfig().getIncludeMetrics(), config.getDimensions());
    }

}
