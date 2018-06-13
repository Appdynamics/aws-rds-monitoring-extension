/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.rds;

import com.amazonaws.services.cloudwatch.model.Metric;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.List;

/**
 * @author Satish Muddam
 */
public class DBIdentifiersMatcherPredicate implements Predicate<Metric> {

    private List<String> includeDBIdentifiers;
    private Predicate<CharSequence> patternPredicate = new Predicate<CharSequence>() {
        @Override
        public boolean apply(CharSequence charSequence) {
            return false;
        }
    };

    public DBIdentifiersMatcherPredicate(List<String> includeDBIdentifiers) {
        this.includeDBIdentifiers = includeDBIdentifiers;
        build();
    }

    private void build() {
        if (includeDBIdentifiers != null && !includeDBIdentifiers.isEmpty()) {
            for (String pattern : includeDBIdentifiers) {
                Predicate<CharSequence> charSequencePredicate = Predicates.containsPattern(pattern);
                if (patternPredicate == null) {
                    patternPredicate = charSequencePredicate;
                } else {
                    patternPredicate = Predicates.or(patternPredicate, charSequencePredicate);
                }
            }
        }
    }

    public boolean apply(Metric metric) {

        String dbIdentifier = metric.getDimensions().get(0).getValue();

        return patternPredicate.apply(dbIdentifier);
    }

    public Predicate<CharSequence> getPatternPredicate() {
        return patternPredicate;
    }
}
