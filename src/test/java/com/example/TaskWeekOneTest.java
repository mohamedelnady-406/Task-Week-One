package com.example;

import io.micronaut.context.annotation.Property;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
@MicronautTest(environments = "test", propertySources = "classpath:application-test.properties",startApplication = false)
@Property(name = "datasources.default.enabled", value = "false")
@Disabled("Excluded from normal test runs")
class TaskWeekOneTest {

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

}
