/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.helloworld;

import com.google.inject.AbstractModule;
import de.ppi.deepsampler.provider.guice.DeepSamplerModule;

public class HelloWorldGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PersonDao.class).to(PersonDaoImpl.class);
        install(new DeepSamplerModule());
    }
}
