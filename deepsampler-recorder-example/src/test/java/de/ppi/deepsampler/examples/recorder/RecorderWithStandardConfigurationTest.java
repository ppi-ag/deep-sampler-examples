/*
 * Copyright 2020 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.recorder;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.ppi.deepsampler.core.api.Matchers;
import de.ppi.deepsampler.core.api.PersistentSample;
import de.ppi.deepsampler.core.api.Sampler;
import de.ppi.deepsampler.examples.helloworld.GreetingService;
import de.ppi.deepsampler.examples.helloworld.PersonDaoImpl;
import de.ppi.deepsampler.junit.*;
import de.ppi.deepsampler.junit5.DeepSamplerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DeepSamplerExtension.class)
class RecorderWithStandardConfigurationTest {

    public static final String EXPECTED_RECORDED_FILE = "de/ppi/deepsampler/examples/recorder/" +
            "RecorderWithStandardConfigurationTest_aTestThatRecordsASampleAsJsonFile.json";

    @PrepareSampler
    private PersonDaoImpl personDaoSampler;

    @Inject
    private GreetingService greetingService;

    @BeforeEach
    void injectWithGuice() {
        Guice.createInjector(new RecorderExampleGuiceModule()).injectMembers(this);
    }

    /**
     * This test uses the {@link SamplerFixture} {@link StandardConfigurationCompound} to define which method is stubbed.
     * The sample that will be returned by the stub is defined in a Json-file. This file is loaded by the annotation
     * {@link LoadSamples}. The path to the file is generated using the full qualified name of the test method
     * including the class name. The file is loaded from the classpath.
     */
    @Test
    @LoadSamples
    @UseSamplerFixture(StandardConfigurationCompound.class)
    void aTestThatLoadsAnExistingSampleFromJson() {
        // WHEN
        String actualGreeting = greetingService.createGreeting(1);

        // THEN

        // We expect, that the name is 'Jon Luc Picard'. This name is defined in the sample file.
        assertEquals("Hello Jon Luc Picard!", actualGreeting);

        // CROSS CHECK

        // We remove all samples, to be sure, that actualGreeting indeed came from the sample
        Sampler.clear();

        // THEN

        // Since all samples have been removed, we now expect to see the original name from the unstubbed
        // method.
        actualGreeting = greetingService.createGreeting(1);
        assertEquals("Hello Geordi La Forge!", actualGreeting);
    }

    @Test
    @SaveSamples
    @Order(1)
    void aTestThatRecordsASampleAsJsonFile() {
        // GIVEN
        Paths.get(EXPECTED_RECORDED_FILE).toFile().delete();
        assertThat(Paths.get(EXPECTED_RECORDED_FILE)).doesNotExist();

        // WHEN
        String actualGreeting = greetingService.createGreeting(1);
        // THEN
        assertEquals("Hello Geordi La Forge!", actualGreeting);
    }

    @Test
    @Order(2)
    void recordedFileCanBeFound() {
        assertThat(Paths.get(EXPECTED_RECORDED_FILE)).exists();
    }


    public static class StandardConfigurationCompound implements SamplerFixture {
        @PrepareSampler
        private PersonDaoImpl personDaoImplSampler;

        @Override
        public void defineSamplers() {
            PersistentSample.of(personDaoImplSampler.loadPerson(Matchers.anyInt())).hasId("loadPerson");
        }
    }

}