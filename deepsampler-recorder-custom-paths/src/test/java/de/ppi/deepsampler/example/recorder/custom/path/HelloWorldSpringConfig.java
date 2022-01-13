/*
 *
 * Copyright 2020 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 *
 */

package de.ppi.deepsampler.example.recorder.custom.path;

import de.ppi.deepsampler.examples.helloworld.GreetingService;
import de.ppi.deepsampler.examples.helloworld.PersonDao;
import de.ppi.deepsampler.examples.helloworld.PersonDaoImpl;
import de.ppi.deepsampler.examples.helloworld.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * (1) DeepSampler uses an AOP-Aspect, so we need to activate AOP using the following annotation:
 */
@EnableAspectJAutoProxy
@Configuration
public class HelloWorldSpringConfig {

    /**
     * (2) This defines the Aspect that we want to use. It is a subclass of DeepSampler's
     * {@link de.ppi.deepsampler.provider.spring.SpringSamplerAspect} which is the central Aspect, that is responsible
     * for the actual stubbing. Take a look into {@link HelloWorldSpringSamplerAspect}
     * to see how Pointcuts are used to define which classes of an application can be stubbed by DeepSampler.
     * <p>
     * Notice: You must use the exact class that contains your {@link org.aspectj.lang.annotation.Pointcut}-annotations
     * as return value here, otherwise Spring refuses to use the @Pointcuts. So you cannot use the abstract parent class
     * {@link de.ppi.deepsampler.provider.spring.SpringSamplerAspect} as a return type here, even though this would be
     * quite common.
     */
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
