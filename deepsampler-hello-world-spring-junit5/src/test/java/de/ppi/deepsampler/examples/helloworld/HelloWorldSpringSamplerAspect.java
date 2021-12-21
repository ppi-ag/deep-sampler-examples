/*
 *
 *  * Copyright 2020 PPI AG (Hamburg, Germany)
 *  * This program is made available under the terms of the MIT License.
 *
 */

package de.ppi.deepsampler.examples.helloworld;

import de.ppi.deepsampler.provider.spring.SpringSamplerAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

// (1) In order to inform Spring that this class is an Aspect, we annotate the class like so:
@Aspect
public class HelloWorldSpringSamplerAspect extends SpringSamplerAspect {

    // (2) This method is used only to attach the Pointcut annotation to it. This annotation defines which classes
    // can be stubbed by DeepSampler. For now, we include all classes in the package de.ppi.deepsampler.examples.helloworld
    // including all sub packages. But there are some classes that will be ignored by DeepSampler to meet some Spring-AOP requirements.
    // So final classes, Enums, Aspects and SpringConfigs will be ignored by default.
    // A short introduction in Pointcut expressions can be found here: https://www.baeldung.com/spring-aop-pointcut-tutorial.
    @Pointcut("within(de.ppi.deepsampler.examples.helloworld..*)")
    @Override
    public void include() {}
}
