/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.example.recorder.matchers;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.ppi.deepsampler.core.api.Matchers;
import de.ppi.deepsampler.core.api.PersistentSample;
import de.ppi.deepsampler.core.error.InvalidConfigException;
import de.ppi.deepsampler.examples.helloworld.GreetingService;
import de.ppi.deepsampler.examples.helloworld.PersonDaoImpl;
import de.ppi.deepsampler.examples.helloworld.PersonId;
import de.ppi.deepsampler.junit.PrepareSampler;
import de.ppi.deepsampler.junit.SampleRootPath;
import de.ppi.deepsampler.junit.SamplerFixture;
import de.ppi.deepsampler.junit.UseSamplerFixture;
import de.ppi.deepsampler.junit.json.LoadSamples;
import de.ppi.deepsampler.junit.json.SaveSamples;
import de.ppi.deepsampler.junit5.DeepSamplerExtension;
import de.ppi.deepsampler.persistence.api.PersistentMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.ppi.deepsampler.persistence.api.PersistentMatchers.anyRecorded;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Premise: This is an advanced example. If you are not familiar with recording yet, you might want to take a look at
 * {@link de.ppi.deepsampler.examples.recorder.RecorderWithStandardConfigurationTest}
 * <p>
 * <p>
 * <p>
 * If a stubbed method is called, DeepSampler uses the parameter values, that have been passed to the stubbed method,
 * as a key to find the stubbed return value. Therefore, the parameter values must be comparable. By default,
 * DeepSampler expects all parameter types to override {@link Object#equals(Object)}.
 * <p>
 * Since this is not always the case, {@link de.ppi.deepsampler.core.api.Matchers} can be used, to change how objects are
 * compared.
 * <p>
 * The usage of {@link de.ppi.deepsampler.core.api.Matchers} is quite straight forward, if used <b>without recording</b>:
 * <code>
 * Sample.of(personDaoSampler.loadPerson(Matchers.anyInt())).is(new Person("Sarek"));
 * </code>
 * The example above will always return "Sarek", because {@link Matchers#anyInt()} matches to all possible integers.
 * <p>
 * <p>
 * However, when it comes to loading <b>persistent samples</b>, things tend to get more complex. This is because we now have
 * two different situations, where matchers can appear: Recording and Replay.
 * <ul>
 *     <li>Recording: Which method-calls will be recorded, is decided by a matcher that is used during recording. This is usually
 *     a matcher that accepts all possible parameter values, since we usually want to record all method-calls without any filtering.</li>
 *     <li>Replay: During the execution of tests, that use previously recorded samples, actual calls of stubbed methods
 *     are matched against the recorded samples. This means: If a stubbed method has been called during a test, DeepSampler tries to
 *     find a previously recorded sample for the called method. The sample is identified by the method-name and the concrete
 *     parameters, with which the stubbed method has been called. To do this, a second matcher is needed.</li>
 * </ul>
 * The default replay-matcher is {@link PersistentMatchers#equalsMatcher()}, so if all parameter types override {@link Object#equals(Object)},
 * nothing further has to be done. But if a type comes without equals() (and we cannot simply add one) a custom matcher is necessary.
 * This test demonstrates how this is done.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(DeepSamplerExtension.class)
class RecorderWithCustomMatchersTest {

    public static final Path EXPECTED_RECORDED_FILE = Paths.get("./tmp/samples/sampleWithCustomMatcher.json");

    @Inject
    private GreetingService greetingService;

    @BeforeEach
    void injectWithGuice() {
        Guice.createInjector(new RecorderExampleGuiceModule()).injectMembers(this);
    }

    /**
     * We start by recording a sample file. The stubbed method {@link GreetingService#createGreeting(PersonId)} has
     * a parameter of the type {@link PersonId} and this class does not override {@link Object#equals(Object)}. During
     * recording, we don't need equals(), so until now everything is, as you might already have seen it, in previous examples.
     */
    @Test
    @Order(1)
    @SaveSamples("sampleWithCustomMatcher.json")
    @UseSamplerFixture(CustomMatcherCompound.class)
    void aSampleIsSavedWithCustomJsonSerializer() {
        // 👉 GIVEN
        assertThat(EXPECTED_RECORDED_FILE).doesNotExist();

        // 🧪 WHEN
        final String actualGreeting = greetingService.createGreeting(new PersonId(1));

        // 🔬 THEN
        assertEquals("Hello Geordi La Forge!", actualGreeting);
    }

    /**
     * As we already have seen, {@link PersonId} does not override {@link Object#equals(Object)}, but we now need
     * a way to match the recorded parameter values to the actual parameter values that appear during this test.
     * We tell DeepSampler how to match these parameter values in {@link CustomMatcherCompound} using combo matchers.
     */
    @Test
    @Order(2)
    @LoadSamples(value = "sampleWithCustomMatcher.json")
    @UseSamplerFixture(CustomMatcherCompound.class)
    void aSampleIsLoadedWithCustomJsonDeserializer() {
        // 👉 GIVEN
        assertThat(EXPECTED_RECORDED_FILE).exists();

        // 🧪 WHEN
        final String actualGreeting = greetingService.createGreeting(new PersonId(1));

        // 🔬 THEN
        assertEquals("Hello Geordi La Forge!", actualGreeting);
    }

    /**
     * Last but not least, we demonstrate what happens, if we use standard matchers with {@link PersonId}. Since
     * the equals() method is missing, DeepSampler throws an Exception, that complains about the missing method.
     */
    @Test
    @Order(3)
    @LoadSamples(value = "sampleWithCustomMatcher.json")
    @UseSamplerFixture(StandardMatcherCompound.class)
    void loadingWithStandardMatcherThrowsException() {
        // 👉 GIVEN
        assertThat(EXPECTED_RECORDED_FILE).exists();

        // 🔬 THEN
        assertThatExceptionOfType(InvalidConfigException.class)
                .isThrownBy(() -> greetingService.createGreeting(new PersonId(1)))
                .withMessage("The class de.ppi.deepsampler.examples.helloworld.PersonId must implement equals() "
                             + "if you want to use an de.ppi.deepsampler.core.api.Matchers$EqualsMatcher. "
                             + "Alternatively, you can define a custom matcher, that does not use equals(). This can be done with "
                             + "Matchers.matcher(...), or PersistentMatchers.anyRecorded(PersistentMatcher<T>)");
    }


    /**
     * This is a {@link SamplerFixture} that defines a stubbed method with a custom matcher, that is used during
     * loading and replaying of recorded samples.
     */
    @SampleRootPath("./tmp/samples")
    public static class CustomMatcherCompound implements SamplerFixture {
        @PrepareSampler
        private PersonDaoImpl personDaoImplSampler;

        @Override
        public void defineSamplers() {
            // First we define which method is to be stubbed...
            PersistentSample.of(personDaoImplSampler.loadPerson(
                            // ... then we define a custom matcher, that essentially replaces the equals() method of PersonId during
                            // test execution. The matcher that is used during recording is unchanged. It is still a
                            // matcher that accepts all possible parameter values.
                            anyRecorded(this::personIdMatches)))
                    .hasId("loadFriend");
        }

        /**
         * This method is the custom matcher for {@link PersonId}s, that is used during replay. The method
         * obeys the functional interface {@link de.ppi.deepsampler.persistence.api.PersistentMatcher} and can be
         * used as a method reference.
         *
         * @param left  One of the two {@link PersonId}s that are tested for equality.
         * @param right The other of the two {@link PersonId}s that are tested for equality.
         * @return true if left and right are equal.
         */
        public boolean personIdMatches(final PersonId left, final PersonId right) {
            return left.getId() == right.getId();
        }
    }

    /**
     * This SamplerFixture uses standard matchers. It is used to demonstrate what happens, if standard matchers
     * cannot be used.
     */
    @SampleRootPath("./tmp/samples")
    public static class StandardMatcherCompound implements SamplerFixture {
        @PrepareSampler
        private PersonDaoImpl personDaoImplSampler;

        @Override
        public void defineSamplers() {
            PersistentSample.of(personDaoImplSampler.loadPerson(anyRecorded(PersonId.class)))
                    .hasId("loadFriend");
        }
    }

    /**
     * 🧽 We delete old sample files, before any tests run, in case some old sample files from previous test runs
     * still exist.
     */
    @BeforeAll
    static void clearSamplerFiles() {
        EXPECTED_RECORDED_FILE.toFile().delete();
    }

}