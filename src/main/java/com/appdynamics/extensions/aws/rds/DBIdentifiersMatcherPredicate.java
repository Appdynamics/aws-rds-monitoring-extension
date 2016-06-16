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
    private Predicate<CharSequence> patternPredicate;

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
}
