/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.helloworld;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.ppi.deepsampler.core.api.Sample;
import de.ppi.deepsampler.core.api.Sampler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This example demonstrates, how to use the basics of DeepSampler with the low level api without annotations.
 * Although we still use a JUnitTest as a demonstration, the low level api could also be used completely without
 * JUnit.
 */
class GreetingServiceTest {

    // (1) The PersonDao has methods that we want to stub. To achieve this, we need to create a Sampler for this type,
    // that can be used to tell DeepSampler, which methods shall be stubbed...
    private PersonDao personDaoSampler = Sampler.prepare(PersonDaoImpl.class);

    // (2) The GreetingService is the testee, a compound of objects that use instances of PersonDao's. We inject
    // a GreetingService with all its dependencies using Guice-Injection....
    @Inject
    private GreetingService greetingService;

    @BeforeEach
    void injectWithGuice() {
        // (3) In order to convince Guice to inject members into this test class, we create a Guice-Injector. The Injector
        // uses the DeepSamplerModule to tell Guice that we want to activate the DeepSampler-AOP-Plugin. This plugin
        // will do the actual stubbing.
        Guice.createInjector(new HelloWorldGuiceModule()).injectMembers(this);
    }

    @Test
    void greetingShouldBeGenerated() {

        // (4) Now we define the stub and the sample-value that will be returned by the stub.
        // From now on, the method PersonDao::loadPerson() will return
        // a Person-Object with the name "Sarek" if loadPerson() is called with a 1 as parameter.
        // This applies to all instances of PersonDao that have been created by Guice, no matter where
        // in our object tree a particular instance is located.
        Sample.of(personDaoSampler.loadPerson(1)).is(new Person("Sarek"));

        // (5) Finally we can call the method that we want to test. Our stub from above is now used and replaces
        // the original person that will be greeted.
        assertEquals("Hello Sarek!", greetingService.createGreeting(1));

        // (6) Just to ensure, that the stubbing actually changed something, we can now clear the stubs...
        Sampler.clear();

        // ... and test again. Now we expect that the original and unstubbed person will be greeted:
        assertEquals("Hello Geordi La Forge!", greetingService.createGreeting(1));
    }
}