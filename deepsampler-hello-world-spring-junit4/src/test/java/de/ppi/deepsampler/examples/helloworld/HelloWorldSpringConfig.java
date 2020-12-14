/*
 *
 *  * Copyright 2020 PPI AG (Hamburg, Germany)
 *  * This program is made available under the terms of the MIT License.
 *
 */

package de.ppi.deepsampler.examples.helloworld;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
// (1) DeepSampler uses an AOP-Aspect, so we need to activate AOP using the following annotation:
@EnableAspectJAutoProxy
public class HelloWorldSpringConfig {

    // (2) This defines the Aspect that we want to use. It is a subclass of DeepSampler's SpringSamplerAspect
    // which is the central Aspect, that is responsible for the actual stubbing. Take a look into HelloWorldSpringSamplerAspect
    // to see how Pointcuts are used to define which classes of an application can be stubbed by DeepSampler.
    @Bean
    public HelloWorldSpringSamplerAspect helloWorldSpringSamplerAspect() {
        return new HelloWorldSpringSamplerAspect();
    }

    @Bean
    public GreetingService greetingService() {
        return new GreetingService();
    }

    @Bean
    public PersonService personService() {
        return new PersonService();
    }

    @Bean
    public PersonDao personDao() {
        return new PersonDaoImpl();
    }
}
