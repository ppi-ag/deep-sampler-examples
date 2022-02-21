/*
 * Copyright 2022 PPI AG (Hamburg, Germany)
 * This program is made available under the terms of the MIT License.
 */

package de.ppi.deepsampler.examples.recorder.beanconverter;

import com.google.inject.AbstractModule;
import de.ppi.deepsampler.examples.helloworld.PersonDao;
import de.ppi.deepsampler.examples.helloworld.PersonDaoImpl;
import de.ppi.deepsampler.provider.guice.DeepSamplerModule;

public class RecorderExampleGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PersonDao.class).to(PersonDaoImpl.class);
        install(new DeepSamplerModule());
    }
}
