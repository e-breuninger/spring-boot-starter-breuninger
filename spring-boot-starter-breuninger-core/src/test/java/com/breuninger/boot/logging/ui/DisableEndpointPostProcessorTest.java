package com.breuninger.boot.logging.ui;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class DisableEndpointPostProcessorTest {

  private AnnotationConfigApplicationContext ctx;

  @Before
  public void setup() {
    ctx = new AnnotationConfigApplicationContext();
  }

  @After
  public void close() {
    if (ctx != null) {
      ctx.close();
    }
  }

  @Test
  public void shouldRegisterBean() {
    ctx.register(TestEndpointConfiguration.class);
    ctx.refresh();
    assertThat(ctx.containsBean("someTestMvcEndpoint"), is(true));
  }

  @Test
  public void shouldDisableEndpoint() {
    TestPropertyValues.of("endpoints.someTest.enabled=true").applyTo(ctx);
    ctx.register(TestEndpointConfiguration.class);
    ctx.register(RemoveTestEndpointConfiguration.class);
    ctx.refresh();
    assertThat(ctx.getEnvironment().getProperty("endpoints.someTest.enabled"), is("false"));
  }

  @Configuration
  static class TestEndpointConfiguration {
    @Bean
    Object someTestMvcEndpoint() {
      return new Object();
    }
  }

  @Configuration
  static class RemoveTestEndpointConfiguration {
    @Bean
    DisableEndpointPostProcessor withoutSomeBean() {
      return new DisableEndpointPostProcessor("someTest");
    }
  }
}
