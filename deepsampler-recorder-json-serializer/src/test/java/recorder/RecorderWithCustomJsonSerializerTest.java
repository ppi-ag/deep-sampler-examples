/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package recorder;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.ppi.deepsampler.core.api.PersistentSample;
import de.ppi.deepsampler.core.api.Sampler;
import de.ppi.deepsampler.examples.helloworld.GreetingService;
import de.ppi.deepsampler.examples.helloworld.Person;
import de.ppi.deepsampler.examples.helloworld.PersonDaoImpl;
import de.ppi.deepsampler.junit.*;
import de.ppi.deepsampler.junit5.DeepSamplerExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static de.ppi.deepsampler.core.api.Matchers.anyInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * By default, DeepSampler uses Jackson as underlying persistence API to write samples in the format of JSON.
 * Jackson can be customized, using custom serializers and deserializers. DeepSampler allows to register these
 * jackson-specific serializers. This is usually helpful, if objects needs to be recorded, that cannot be serialized
 * by Jackson's default serialization. This test shows how this is done.
 *
 * As a first step, we register the {@link SamplerFixture} {@link StarDateCompound} for the complete test.
 * {@link StarDateCompound} is then used to define the serializers.
 */
@UseSamplerFixture(RecorderWithCustomJsonSerializerTest.StarDateCompound.class)

@ExtendWith(DeepSamplerExtension.class)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecorderWithCustomJsonSerializerTest {

    public static final Path EXPECTED_RECORDED_FILE = Paths.get("./tmp/samples/sampleWithStarDate.json");

    @PrepareSampler
    private PersonDaoImpl personDaoSampler;

    @Inject
    private GreetingService greetingService;

    @BeforeEach
    void injectWithGuice() {
        Guice.createInjector(new RecorderExampleGuiceModule()).injectMembers(this);
    }

    /**
     * This test saves a {@link de.ppi.deepsampler.examples.helloworld.Person} that contains a birthday-attribute with
     * the type {@link java.time.LocalDateTime}. By default, LocalDateTime is persisted using a single numeric value.
     * Since this is not a human-readable format, we change the format to a stardate. This is done using
     * {@link StarDateJsonSerializer} and the {@link StarDateCompound}.
     * <p>
     * Notice: the execution order of the tests is fixed, so that the first test can save a sampler, that is read by the
     * second test.
     */
    @Test
    @Order(1)
    @SaveSamples("sampleWithStarDate.json")
    void aSampleIsSavedWithCustomJsonSerializer() {
        // 👉 GIVEN
        assertThat(EXPECTED_RECORDED_FILE).doesNotExist();

        // 🧪 WHEN
        String actualGreeting = greetingService.createGreeting(1);

        // 🔬 THEN
        assertEquals("Hello Geordi La Forge!", actualGreeting);
    }

    /**
     * The preceding test has saved a sampler in <code>sampleWithStarDate.json</code>. We now try to load this sampler.
     * Since {@link Person#getBirthday()} was serialized as a stardate, we need to use the {@link StarDateJsonDeserializer},
     * for deserializing. Since {@link StarDateJsonDeserializer} is already registered at {@link StarDateCompound},
     * we don't have to do anything special here anymore.
     */
    @Test
    @Order(2)
    @LoadSamples(value = "sampleWithStarDate.json")
    void aSampleIsLoadedWithCustomJsonDeserializer() {
        // 👉 GIVEN
        assertThat(EXPECTED_RECORDED_FILE).exists();

        // 🔬 THEN
        // To be sure, that LocalDateTime has indeed been converted to a stardate, we check this, using a regexp on the
        // actual JSON-file:
        assertThat(EXPECTED_RECORDED_FILE).content().contains("\"0$birthday\" : [ \"java.time.LocalDateTime\", \"2335047.0000\" ]");

        String actualBirthdayGreeting = greetingService.createBirthdayGreeting(1);
        // And here we check, that LocalDateTime was correctly deserialized and converted to a LocalDateTime:
        assertThat(actualBirthdayGreeting).isEqualTo("Geordi La Forge's Birthday: 16.02.2335");
    }

    /**
     * 🧽 We delete old sample files, before any tests run, in case some old sample files from previous test runs
     * still exist.
     */
    @BeforeAll
    static void clearSamplerFiles() {
        EXPECTED_RECORDED_FILE.toFile().delete();
    }


    /**
     * We annotate this {@link SamplerFixture} with {@link UseJsonSerializer} and {@link UseJsonDeserializer} to activate
     * the custom serializers for all tests, that use this {@link SamplerFixture}. DeepSampler will now pass the serializers
     * to Jackson.
     */
    @UseJsonSerializer(serializer = StarDateJsonSerializer.class, forType = LocalDateTime.class)
    @UseJsonDeserializer(deserializer = StarDateJsonDeserializer.class, forType = LocalDateTime.class)

    @SampleRootPath("./tmp/samples")
    public static class StarDateCompound implements SamplerFixture {
        @PrepareSampler
        private PersonDaoImpl personDaoImplSampler;

        @Override
        public void defineSamplers() {
            // Here we define the sampled method. If we run a test using @LoadSamples, the return value of this method
            // will be taken from a JSON-file, the original method is not called anymore.
            // If we run a test using @SaveSamples, the original method will be called, and parameter values, as well as the
            // return value, will be intercepted and saved to a Json-file.
            PersistentSample.of(personDaoImplSampler.loadPerson(anyInt())).hasId("loadPerson");
        }
    }

}