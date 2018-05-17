/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.rds;

import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class RDSMonitorITest {

	private RDSMonitor classUnderTest = new RDSMonitor();

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
