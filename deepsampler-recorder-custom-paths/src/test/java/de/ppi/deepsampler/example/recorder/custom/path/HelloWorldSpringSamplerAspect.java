/*
 *
 * Copyright 2020 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 *
 */

package de.ppi.deepsampler.example.recorder.custom.path;

import de.ppi.deepsampler.provider.spring.SpringSamplerAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * (1) In order to inform Spring that this class is an Aspect, we annotate the class like so:
 */
@Aspect
public class HelloWorldSpringSamplerAspect extends SpringSamplerAspect {
    /**
     * (2) This method is used only to attach the {@link Pointcut} annotation to it. This annotation defines which classes
     * can be stubbed by DeepSampler. For now, we include all classes in the package de.ppi.deepsampler.examples.helloworld
     * including all sub packages. There are some classes that will be ignored by DeepSampler, though. This is often
     * necessary, because Spring AOP has some limitations. So final classes, Enums, Aspects and SpringConfigs will be
     * ignored by default. You can change the default by overriding {@link SpringSamplerAspect#defaultPointCut()}
     * <p>
     * A short introduction to Pointcut expressions can be found here:
     * <a href="https://www.baeldung.com/spring-aop-pointcut-tutorial">Introduction to Pointcut Expressions in Spring</a>.
     */
    @Pointcut("within(de.ppi.deepsampler.examples.helloworld..*)")
    @Override
    public void include() {
    }
}
