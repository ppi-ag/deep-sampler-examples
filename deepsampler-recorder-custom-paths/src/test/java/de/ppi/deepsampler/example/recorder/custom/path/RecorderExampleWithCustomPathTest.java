/*
 * Copyright 2020 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.example.recorder.custom.path;

import de.ppi.deepsampler.core.api.PersistentSample;
import de.ppi.deepsampler.core.model.SampleRepository;
import de.ppi.deepsampler.examples.helloworld.GreetingService;
import de.ppi.deepsampler.examples.helloworld.PersonDao;
import de.ppi.deepsampler.junit.*;
import de.ppi.deepsampler.junit5.DeepSamplerExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This example demonstrates, how to use custom names for recorded sample-files instead of the default generated names.
 * <p>
 * File names are constructed from two elements [SampleRootPath]/[FileName] where
 * <ul>
 *     <li>SampleRootPath is defined using the Annotation {@link SampleRootPath}. It can be used to set a root path for
 *     all sample files of a test or a compound</li>
 *     <li>FileName is defined using the Annotations {@link SaveSamples} or {@link LoadSamples}. It can be used to define
 *     a custom filename and/or path for the sampler</li>
 * </ul>
 * <p>
 * <p>
 * 🚀 We start by setting the root path for all tests in this class, by annotating the class with {@link SampleRootPath}.
 * If you prefer to define the same root for more than one test class, you might want to use the annotation on
 * a {@link SamplerFixture}, so that the root is applied to all tests, that use this {@link SamplerFixture}.
 */
@SampleRootPath("./src/test/resources")

@UseSamplerFixture(RecorderExampleWithCustomPathTest.MyCompound.class)
@ExtendWith({DeepSamplerExtension.class, SpringExtension.class})

@ContextConfiguration(classes = HelloWorldSpringConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecorderExampleWithCustomPathTest {

    private static final Path SAMPLER_FILE = Paths.get("./src/test/resources/mySamples.json");

    @Inject
    private GreetingService greetingService;

    /**
     * 🧪 The first test records a sample file. The file's name is now defined by the value-attribute of
     * {@link SaveSamples}. The supplied file name is resolved against the {@link SampleRootPath}. The file will
     * now be saved as ./src/test/resources/mySamples.json.
     * <p>
     * Notice: The execution order of the test methods is fixed, so that this test method is always called first.
     */
    @Test
    @Order(0)
    @SaveSamples("mySamples.json")
    void whenSamplerIsSavedInCustomizedPath() {
        assertThat(SAMPLER_FILE).doesNotExist();
        greetingService.createGreeting(1);
    }

    /**
     * 🔬 When {@link RecorderExampleWithCustomPathTest#whenSamplerIsSavedInCustomizedPath()} has finished, we
     * expect to find the sample file, that should have been written by it.
     * <p>
     * {@link LoadSamples} is able to load the file either from the file system,
     * or from the classpath. Both options may result in different paths for the same file:
     * <ul>
     *     <li>File system: The full file name is constructed from two elements: [SampleRootPath]/[FileName]</li>
     *     <li>Classpath: {@link SampleRootPath} is ignored, since the root folder is defined by the classpath.
     *     The file name, that is passed to {@link LoadSamples}, is interpreted as a classpath resource exactly in the
     *     same way as it is described by {@link ClassLoader#getResource(String)}</li>
     * </ul>
     * <p>
     * Notice: The execution order of the test method is fixed, so that this test method is always called last.
     */
    @Test
    @Order(1)
    @LoadSamples(value = "mySamples.json", source = FileSource.FILE_SYSTEM)
    void thenCustomSamplerFileNameCanBeUsedByAnotherMethod() {
        assertThat(SAMPLER_FILE).exists();
        assertThat(SampleRepository.getInstance().isEmpty()).isFalse();
    }

    /**
     * 🧽 We delete old sample files, before any tests run, in case some old sample files from previous test runs
     * still exist.
     */
    @BeforeAll
    static void clearSamplerFiles() {
        SAMPLER_FILE.toFile().delete();
    }

    public static class MyCompound implements SamplerFixture {
        @PrepareSampler
        private PersonDao personDaoSampler;

        @Override
        public void defineSamplers() {
            PersistentSample.of(personDaoSampler.loadPerson(1)).hasId("loadPerson");
        }
    }

}
