/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.helloworld;

import de.ppi.deepsampler.core.api.Sample;
import de.ppi.deepsampler.core.api.Sampler;
import de.ppi.deepsampler.junit.PrepareSampler;
import de.ppi.deepsampler.junit5.DeepSamplerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

/**
 * This example demonstrates, how to use the basics of DeepSampler with JUnit5 and Spring.
 */
// (1) We start by defining two extensions: First the SpringExtension, and second the DeepSamplerExtension. The order
// of the extensions is not important.

// (2) Now we need to set up the Spring-context. Please take a look into the class HelloWorldSpringConfig to understand
// how DeepSampler is activated in a Spring-environment.
@ExtendWith({DeepSamplerExtension.class, SpringExtension.class})
@ContextConfiguration(classes = HelloWorldSpringConfig.class)
class GreetingServiceTest {

    // (3) The PersonDao has methods that we want to stub, so we need to prepare a Sampler for that class
    // by using the annotation @PrepareSampler...
    @PrepareSampler
    private PersonDao personDaoSampler;

    // (4) The GreetingService is the testee, a compound of objects that use instances of PersonDao's. We inject
    // a GreetingService with all its dependencies using Spring-Injection.
    @Inject
    private GreetingService greetingService;

  
    @Test
    void greetingShouldBeGenerated() {
        // (5) Now we define the stub and the sample-value that will be returned by the stub.
        // From now on, the method PersonDao::loadPerson() will return
        // a Person-Object with the name "Sarek" if loadPerson() is called with a 1 as parameter.
        // This applies to all instances of PersonDao that have been created by Spring, no matter where
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
