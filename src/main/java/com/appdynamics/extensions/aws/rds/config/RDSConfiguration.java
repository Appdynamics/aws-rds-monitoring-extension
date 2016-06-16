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
