/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.rds.config;

import com.appdynamics.extensions.aws.config.Configuration;

import java.util.List;

/**
 * @author Satish Muddam
 */
public class RDSConfiguration extends Configuration {

    private List<String> includeDBIdentifiers;

    public List<String> getIncludeDBIdentifiers() {
        return includeDBIdentifiers;
    }

    public void setIncludeDBIdentifiers(List<String> includeDBIdentifiers) {
        this.includeDBIdentifiers = includeDBIdentifiers;
    }
}
