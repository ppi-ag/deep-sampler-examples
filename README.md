![Build & Test](https://github.com/ppi-ag/deep-sampler-examples/workflows/Build%20&%20Test/badge.svg)
# 🎓 deep-sampler-examples
A collection of executable examples for [DeepSampler 2.1.0](https://github.com/ppi-ag/deep-sampler)

If you are using the previous version 
   * 2.0.0 see [DeepSampler 2.0.0 examples](https://github.com/ppi-ag/deep-sampler-examples/tree/%F0%9F%93%9A-maintenance-v2.0.0)
   * 1.1.0 see [DeepSampler 1.1.0 examples](https://github.com/ppi-ag/deep-sampler-examples/tree/%F0%9F%93%9A-maintenance-v1.1.0)

# Content
1. __Basic setup:__ The following examples demonstrate how to set up the basics for JUnit-tests with deepsampler. 
They also show, how methods can be stubbed using the `Sample`-API. 
   1. [JUnit4 & Spring](deepsampler-hello-world-spring-junit4/src/test/java/de/ppi/deepsampler/examples/helloworld)
   2. [JUnit4 & Guice](deepsampler-hello-world-guice-junit4/src/test/java/de/ppi/deepsampler/examples/helloworld)
   3. [JUnit5 & Spring](deepsampler-hello-world-spring-junit5/src/test/java/de/ppi/deepsampler/examples/helloworld)
   4. [JUnit5 & Guice](deepsampler-hello-world-guice-junit5/src/test/java/de/ppi/deepsampler/examples/helloworld)
2. __Recording & Loading samples:__ The following examples demonstrate, how test data (samples) can be recorded at
runtime, and how persistent samples can be loaded in tests. 
   1. [Saving and loading samples with default configuration](deepsampler-recorder-example/src/test/java/de/ppi/deepsampler/examples/recorder)
   2. [Tweaking filenames and paths](deepsampler-recorder-custom-paths/src/test/java/de/ppi/deepsampler/example/recorder/custom/path)
   3. [Adding custom `JsonSerializer`s and `JsonDeserializer`s to persist data, that cannot be persisted by DeepSampler 
   out of the box](deepsampler-recorder-json-serializer/src/test/java/de/ppi/deepsampler/example/recorder/json/serializer)
   4. [Adding custom `PersistenBeanExtension`s to persist data, that cannot be persisted by DeepSampler
      out of the box](deepsampler-recorder-bean-converter-extension/src/test/java/de/ppi/deepsampler/examples/recorder/beanconverter)
   5. [Using custom matchers](deepsampler-recorder-matchers/src/test/java/de/ppi/deepsampler/example/recorder/matchers)
3. __Using the low-level-api without annotations:__ DeepSampler provides a low-level-api in case it is used 
without JUnit, or special configurations are necessary.
   1. [Defining stubs](deepsampler-hello-world-guice-low-level-api/src/test/java/de/ppi/deepsampler/examples/helloworld)
   2. [Saving and loading samples to/from disk](deepsampler-recorder-low-level-api/src/test/java/de/ppi/deepsampler/example/recorder)


# License
[DeepSampler](https://github.com/ppi-ag/deep-sampler) and deep-sampler-examples are made available under the terms of the __MIT License__ (see [LICENSE.md](./LICENSE.md)).

Copyright 2022 PPI AG (Hamburg, Germany)
