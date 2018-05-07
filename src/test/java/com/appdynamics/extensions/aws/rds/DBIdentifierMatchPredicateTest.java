package com.appdynamics.extensions.aws.rds;

import com.appdynamics.extensions.yml.YmlReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class DBIdentifierMatchPredicateTest {

    List<String> identifier = (List<String>)(YmlReader.readFromFile(new File("src/test/resources/itest-encrypted-config.yml")).get("includeDBIdentifiers"));

    private DBIdentifiersMatcherPredicate classUnderTest = new DBIdentifiersMatcherPredicate(identifier);

    @Test
    public void testPatternPredicate(){
        Assert.assertTrue(classUnderTest.getPatternPredicate().apply(identifier.get(0)));
    }
}
