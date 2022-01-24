![Build & Test](https://github.com/ppi-ag/deep-sampler-examples/workflows/Build%20&%20Test/badge.svg)
# 🎓 deep-sampler-examples
A collection of executable examples for [DeepSampler 2.0.0](https://github.com/ppi-ag/deep-sampler)

If you are using the previous version 1.1.0 see [DeepSampler 1.1.0 examples](https://github.com/ppi-ag/deep-sampler-examples/tree/main)

# Content
1. __Basic setup:__ The following examples demonstrate how to set up the basics for JUnit-tests with deepsampler. 
They also show, how methods can be stubbed using the `Sample`-API. 
   1. [JUnit4 & Spring](deepsampler-hello-world-spring-junit4)
   2. [JUnit4 & Guice](deepsampler-hello-world-guice-junit4)
   3. [JUnit5 & Spring](deepsampler-hello-world-spring-junit5)
   4. [JUnit5 & Guice](deepsampler-hello-world-guice-junit5)
2. __Recording & Loading samples:__ The following examples demonstrate, how test data (samples) can be recorded at
runtime, and how persistent samples can be loaded in tests. 
   1. [Saving and loading samples with default configuration](deepsampler-recorder-example)
   2. [Tweaking filenames and paths](deepsampler-recorder-custom-paths)
   3. Adding custom `JsonSerializer`s and `JsonDeserializer`s to persist data, that cannot be persisted by DeepSampler 
   out of the box
   4. [Adding custom `PersistenBeanExtension`s to persist data, that cannot be persisted by DeepSampler
      out of the box](deepsampler-recorder-bean-converter-extension/src/test/java/de/ppi/deepsampler/examples/recorder/beanconverter)
   5. Using custom matchers
3. __Using the low-level-api without annotations:__ DeepSampler provides a low-level-api in case it is used 
without JUnit, or special configurations are necessary.
   1. [Defining stubs](deepsampler-hello-world-guice-low-level-api)
   2. [Saving and loading samples to/from disk](deepsampler-recorder-low-level-api)

# License
[DeepSampler](https://github.com/ppi-ag/deep-sampler) and deep-sampler-examples are made available under the terms of the __MIT License__ (see [LICENSE.md](./LICENSE.md)).

Copyright 2020 PPI AG (Hamburg, Germany)
