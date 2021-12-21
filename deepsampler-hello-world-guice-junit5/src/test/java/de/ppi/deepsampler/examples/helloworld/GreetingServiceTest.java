/*
 * Copyright 2020 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.helloworld;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.ppi.deepsampler.core.api.Sample;
import de.ppi.deepsampler.core.api.Sampler;
import de.ppi.deepsampler.junit.*;
import de.ppi.deepsampler.junit5.DeepSamplerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This example demonstrates, how to use the basics of DeepSampler with JUnit5 and Guice.
 */
// (1) The DeepSamplerExtension enables annotations from DeepSampler...
@ExtendWith(DeepSamplerExtension.class)
class GreetingServiceTest {

    // (2) The PersonDao has methods that we want to stub, so we need to prepare a Sampler for that class
    // by using the annotation @PrepareSampler...
    @PrepareSampler
    private PersonDaoImpl personDaoSampler;

    // (3) The GreetingService is the testee, a compound of objects that use instances of PersonDao's. We inject
    // a GreetingService with all its dependencies using Guice-Injection....
    @Inject
    private GreetingService greetingService;

    @BeforeEach
    void injectWithGuice() {
        // (4) In order to convince Guice to inject members into this test class, we create a Guice-Injector. The Injector
        // uses the DeepSamplerModule to tell Guice that we want to activate the DeepSampler-AOP-Plugin. This plugin
        // will do the actual stubbing.
        Guice.createInjector(new HelloWorldGuiceModule()).injectMembers(this);
    }

    @Test
    void greetingShouldBeGenerated() {
        // (5) Now we define the stub and the sample-value that will be returned by the stub.
        // From now on, the method PersonDao::loadPerson() will return
        // a Person-Object with the name "Sarek" if loadPerson() is called with a 1 as parameter.
        // This applies to all instances of PersonDao that have been created by Guice, no matter where
        // in our object tree a particular instance is located.
        Sample.of(personDaoSampler.loadPerson(1)).is(new Person("Sarek"));

        // (6) Finally we can call the method that we want to test. Our stub from above is now used and replaces
        // the original person that will be greeted.
        assertEquals("Hello Sarek!", greetingService.createGreeting(1));

        // (7) Just to ensure, that the stubbing actually changed something, we can now clear the stubs...
        Sampler.clear();

        // ... and test again. Now we expect that the original and unstubbed person will be greeted:
        assertEquals("Hello Geordi La Forge!", greetingService.createGreeting(1));
    }
}