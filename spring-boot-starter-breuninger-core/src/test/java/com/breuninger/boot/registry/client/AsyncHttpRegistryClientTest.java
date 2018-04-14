package com.breuninger.boot.registry.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.asynchttpclient.DefaultAsyncHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.breuninger.boot.status.configuration.ApplicationInfoConfiguration;

public class AsyncHttpRegistryClientTest {

  private AnnotationConfigApplicationContext context;

  @Before
  public void setUp() {
    context = new AnnotationConfigApplicationContext();
  }

  @After
  public void close() {
    if (context != null) {
      context.close();
    }
  }

  @Test
  public void shouldDoNothingIfNotEnabled() {
    // given
    TestPropertyValues.of("breuninger.serviceregistry.enabled=false").applyTo(context);
    context.register(DefaultAsyncHttpClient.class);
    context.register(ApplicationInfoConfiguration.class);
    context.register(AsyncHttpRegistryClient.class);
    context.refresh();

    // when
    final var bean = context.getBean(RegistryClient.class);

    assertThat(bean.isRunning(), is(false));
  }

  @Test
  public void shouldHaveRegistryIfServersAndServicePresent() {
    // given
    TestPropertyValues.of("breuninger.serviceregistry.servers=http://foo")
      .and("breuninger.serviceregistry.service=http://test")
      .applyTo(context);
    context.register(DefaultAsyncHttpClient.class);
    context.register(ApplicationInfoConfiguration.class);
    context.register(AsyncHttpRegistryClient.class);
    context.refresh();

    assertThat(context.containsBean("asyncHttpRegistryClient"), is(true));
  }

  @Test
  public void shouldDoNothingIfNoServersAreSet() {
    // given
    TestPropertyValues.of("breuninger.serviceregistry.enabled=true").and("breuninger.serviceregistry.servers=").applyTo(context);
    context.register(DefaultAsyncHttpClient.class);
    context.register(ApplicationInfoConfiguration.class);
    context.register(AsyncHttpRegistryClient.class);
    context.refresh();

    final var bean = context.getBean(RegistryClient.class);

    assertThat(bean.isRunning(), is(false));
  }

  @Test
  public void shouldDoNothingIfNoServiceAreSet() {
    // given
    TestPropertyValues.of("breuninger.serviceregistry.enabled=true").and("breuninger.serviceregistry.service=").applyTo(context);
    context.register(DefaultAsyncHttpClient.class);
    context.register(ApplicationInfoConfiguration.class);
    context.register(AsyncHttpRegistryClient.class);
    context.refresh();

    final var bean = context.getBean(RegistryClient.class);

    assertThat(bean.isRunning(), is(false));
  }

  @Test
  public void shouldDoNothingIfRegistryDisabled() {
    // given
    TestPropertyValues.of("breuninger.serviceregistry.enabled=false")
      .and("breuninger.serviceregistry.servers=http://foo")
      .and("breuninger.serviceregistry.service=http://test")
      .applyTo(context);
    context.register(DefaultAsyncHttpClient.class);
    context.register(ApplicationInfoConfiguration.class);
    context.register(AsyncHttpRegistryClient.class);
    context.refresh();

    final var bean = context.getBean(RegistryClient.class);

    assertThat(bean.isRunning(), is(false));
  }

  @Test
  public void shouldDoNothingIfNothingConfigured() {
    // given
    context.register(DefaultAsyncHttpClient.class);
    context.register(ApplicationInfoConfiguration.class);
    context.register(AsyncHttpRegistryClient.class);
    context.refresh();

    final var bean = context.getBean(RegistryClient.class);

    assertThat(bean.isRunning(), is(false));
  }
}
