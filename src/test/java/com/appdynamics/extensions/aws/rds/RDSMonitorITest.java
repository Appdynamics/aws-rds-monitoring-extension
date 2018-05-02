/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.rds;

import static com.appdynamics.extensions.aws.Constants.METRIC_PATH_SEPARATOR;
import static org.junit.Assert.assertTrue;

import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Map;

public class RDSMonitorITest {

	private RDSMonitor classUnderTest = new RDSMonitor();

	private static final String DEFAULT_METRIC_PREFIX = String.format("%s%s%s%s",
			"Custom Metrics", METRIC_PATH_SEPARATOR, "Amazon RDS", METRIC_PATH_SEPARATOR);

	@Before
	public void init() throws Exception {
		//Mockito.when(classUnderTest.getDefaultMetricPrefix()).thenReturn(DEFAULT_METRIC_PREFIX);
	}

	@Test
	public void testMetricsCollectionCredentialsEncrypted() throws Exception {
		Map<String, String> args = Maps.newHashMap();
		args.put("config-file","src/test/resources/itest-encrypted-config.yml");
		
		TaskOutput result = classUnderTest.execute(args, null);
		assertTrue(result.getStatusMessage().contains("Monitor {} completes"));
	}
	
	@Test
	public void testMetricsCollectionWithProxy() throws Exception {
		Map<String, String> args = Maps.newHashMap();
		args.put("config-file","src/test/resources/itest-proxy-config.yml");
		
		TaskOutput result = classUnderTest.execute(args, null);
		assertTrue(result.getStatusMessage().contains("Monitor {} completes"));
	}	
}
