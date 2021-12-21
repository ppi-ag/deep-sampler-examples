/*
 * Copyright 2020 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.recorder;

import com.google.inject.Guice;
import com.google.inject.Inject;
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

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.ppi.deepsampler.core.api.Matchers.anyInt;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test class shows how samples can be recorded from a runtime example and how a previous recorded sample file
 * can be loaded. We are using the default configurations in this example. E.g. things like folders and filenames are
 * defined by DeepSampler automatically.
 */
@ExtendWith(DeepSamplerExtension.class)
class RecorderWithStandardConfigurationTest {

    public static final Path EXPECTED_RECORDED_FILE = Paths.get("de/ppi/deepsampler/examples/recorder/" +
            "RecorderWithStandardConfigurationTest_aTestThatRecordsASampleAsJsonFile.json");

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
        // 🧪 WHEN
        String actualGreeting = greetingService.createGreeting(1);

        // 🔬 THEN
        // We expect, that the name is 'Jon Luc Picard'. This name is defined in the sample file.
        assertEquals("Hello Jon Luc Picard!", actualGreeting);

        // 🔭 CROSS CHECK
        // We remove all samples, to be sure, that actualGreeting indeed came from the sample
        Sampler.clear();

        // 🔬 THEN
        // Since all samples have been removed, we now expect to see the original name from the unstubbed
        // method.
        actualGreeting = greetingService.createGreeting(1);
        assertEquals("Hello Geordi La Forge!", actualGreeting);
    }

    /**
     * This test shows, how a sample file can be recorded from a runtime example. The method, that should be
     * recorded, is defined using the {@link SamplerFixture} {@link StandardConfigurationCompound}.
     * The annotation {@link SaveSamples} tells DepSampler to save every call to the stubbed method, including the
     * parameter values and the return value to a file.
     * <p>
     * The file will be saved after the test method has finished.
     * <p>
     * For the sake of demonstration, we want to proof, that indeed
     * a file is written after the test. We do this, using a second test method (recordedFileCanBeFound()). To be sure,
     * that this second method is executed after aTestThatRecordsASampleAsJsonFile(), we define the execution order by
     * JUnit's annotation {@link Order}.
     */
    @Test
    @SaveSamples
    @UseSamplerFixture(StandardConfigurationCompound.class)
    @Order(1)
    void aTestThatRecordsASampleAsJsonFile() {
        // 👉 GIVEN
        EXPECTED_RECORDED_FILE.toFile().delete();
        assertThat(EXPECTED_RECORDED_FILE).doesNotExist();

        // 🧪 WHEN
        // The following method calls the sampled method, which we want to record using @SaveSamples...:
        String actualGreeting = greetingService.createGreeting(1);

        // 🔬 THEN
        // actualGreeting must contain the unstubbed name, as it has been generated by the unstubbed method.
        assertEquals("Hello Geordi La Forge!", actualGreeting);
    }

    @Test
    @Order(2)
    void recordedFileCanBeFound() {
        // 🔬 THEN
        // The preceding method should have written a json file:
        assertThat(EXPECTED_RECORDED_FILE).exists();
    }


    public static class StandardConfigurationCompound implements SamplerFixture {
        @PrepareSampler
        private PersonDaoImpl personDaoImplSampler;

        @Override
        public void defineSamplers() {
            // Here we define the sampled method. If we run a test using @LoadSamples, the return value of this method
            // will be taken from a JSON-file, the original method is not called anymore.
            // If we run a test using @SaveSamples, the orignal method will called ant the parameter values, and the
            // return value will be saved to a Json-file.
            PersistentSample.of(personDaoImplSampler.loadPerson(anyInt())).hasId("loadPerson");
        }
    }

}