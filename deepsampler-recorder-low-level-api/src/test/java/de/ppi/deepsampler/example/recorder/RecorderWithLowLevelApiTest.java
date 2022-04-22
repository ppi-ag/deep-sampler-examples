/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.example.recorder;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.ppi.deepsampler.core.api.PersistentSample;
import de.ppi.deepsampler.core.api.Sampler;
import de.ppi.deepsampler.examples.helloworld.GreetingService;
import de.ppi.deepsampler.examples.helloworld.PersonDao;
import de.ppi.deepsampler.examples.helloworld.PersonDaoImpl;
import de.ppi.deepsampler.persistence.api.PersistentSampler;
import de.ppi.deepsampler.persistence.api.SourceManager;
import de.ppi.deepsampler.persistence.json.JsonSourceManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.ppi.deepsampler.persistence.api.PersistentMatchers.anyRecordedInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test class shows how to use the low-level-api to record samples from a runtime example and how to load
 * previously recorded sample files.
 *
 * Notice: the execution order of the tests is fixed, so that a preceding test can record a sample file and a
 * subsequent test can load this file.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecorderWithLowLevelApiTest {

    public static final Path RECORDED_SAMPLER = Paths.get("./tmp/MySampler.json");

    // (1) The PersonDao has methods that we want to stub. To achieve this, we create a sampler for this DAO:
    private final PersonDao personDaoSampler = Sampler.prepare(PersonDaoImpl.class);


    @Inject
    private GreetingService greetingService;
    @Inject
    private PersonDao personDao;

    // (2) We use Guice for dependency injection and AOP in this example. The RecorderExampleGuiceModule registers the
    // DeepSamplerModule which is used to enable AOP and stubbing with Guice.
    @BeforeEach
    void injectWithGuice() {
        Guice.createInjector(new RecorderExampleGuiceModule()).injectMembers(this);
    }

    @Test
    @Order(0)
    void aTestThatRecordsASampleAsJsonFile() {
        // 👉 GIVEN

        assertThat(RECORDED_SAMPLER).doesNotExist();

        // (3) We define the stubbed method. Notice, how we use a wildcard for the parameter value. We do
        // this to record all calls to the method, no matter with which parameter values it is called. The actual
        // parameter value will also be recorded to the sample file, so that the sample return value can later be
        // mapped to the parameter value.
        PersistentSample.of(personDaoSampler.loadPerson(anyRecordedInt())).hasId("loadPerson");

        // 🧪 WHEN
        // (4) We now call a method that in turn calls the method that we want to stub. DeepSampler intercepts the
        // call to PersonDao::loadPerson() and stores the parameter values and the return value as a MethodCall.
        // The hardcoded default name of our person is "Geordi La Forge". We change this, so that the subsequent test
        // can demonstrate that the method is actually returning a sample value from the file.
        personDao.setName("Data");
        final String actualGreeting = greetingService.createGreeting(1);

        // 🔬 THEN
        // Just for the sake of demonstration, we check that actualGreeting contains the unstubbed name, as it has been
        // generated by the unstubbed method.
        assertEquals("Hello Data!", actualGreeting);

        // (5) We create a SourceManager that is able to write samples to JSON-Files.
        final SourceManager jsonSourceManager = JsonSourceManager.builder().buildWithFile(RECORDED_SAMPLER);
        // (6) The SourceManager is now used, to actually save the MethodCall, that has been intercepted in step (4).
        PersistentSampler.source(jsonSourceManager).recordSamples();
    }

    @Test
    @Order(1)
    void recordedFileCanBeFound() {
        // 👉 GIVEN

        // (7) The preceding method should have written a json file:
        assertThat(RECORDED_SAMPLER).exists();

        // (8) We still should see the unstubbed default value, since we didn't load any samples yet... Remember, we
        // changed the name to "Data" in step 4, so that "Data" was recorded.
        final String unstubbedGreeting = greetingService.createGreeting(1);
        assertEquals("Hello Geordi La Forge!", unstubbedGreeting);

        // (9) We define which method we want to stub...
        PersistentSample.of(personDaoSampler.loadPerson(anyRecordedInt())).hasId("loadPerson");

        // (10) and we load the sample for the stubbed method from a file. Again, we need a SourceManager, that is able to
        // read samples from JSON-Files.
        final SourceManager jsonSourceManager = JsonSourceManager.builder().buildWithFile(RECORDED_SAMPLER);
        // (11) The SourceManager is now used, to actually load samples from the file, that has been recorded by the
        // preceding test.
        PersistentSampler.source(jsonSourceManager).load();

        // 🧪 WHEN

        // (12) We call the stubbed method again. Now we should get the sample value from the file:
        final String stubbedGreeting = greetingService.createGreeting(1);

        // 🔬 THEN
        assertEquals("Hello Data!", stubbedGreeting);
    }

    /**
     * 🧽 We delete old sample files, before any tests run, in case some old sample files from previous test runs
     * still exist.
     */
    @BeforeAll
    static void clearSamplerFiles() {
        RECORDED_SAMPLER.toFile().delete();
    }

}