package com.breuninger.boot.status.controller;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.mockito.Mockito.when;
import static org.springframework.boot.SpringApplication.run;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.breuninger.boot.status.domain.Criticality.criticality;
import static com.breuninger.boot.status.domain.Datasource.datasource;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.mongoDependency;
import static com.breuninger.boot.status.domain.Expectations.lowExpectations;
import static com.breuninger.boot.status.domain.Level.HIGH;
import static com.breuninger.boot.status.domain.ServiceDependencyBuilder.restServiceDependency;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DependenciesController.class)
@Import({WebEndpointProperties.class, BreuningerApplicationProperties.class})
public class ExternalDependenciesTest {

  @MockBean
  private ExternalDependencies externalDependencies;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldReturnEmptyDependencies() throws Exception {
    when(externalDependencies.getDependencies()).thenReturn(emptyList());
    mockMvc.perform(get("/internal/dependencies").accept(APPLICATION_JSON))
      .andExpect(status().is2xxSuccessful())
      .andExpect(jsonPath("@.dependencies").isArray())
      .andExpect(jsonPath("@.dependencies").isEmpty());
  }

  @Test
  public void shouldReturnDependencies() throws Exception {
    when(externalDependencies.getDependencies()).thenReturn(
      asList(mongoDependency(singletonList(datasource("foo:42/bar"))).withName("test").build(),
        restServiceDependency("foobar:4711").build()));
    mockMvc.perform(get("/internal/dependencies").accept(APPLICATION_JSON))
      .andExpect(status().is2xxSuccessful())
      .andExpect(jsonPath("@.dependencies").isArray())
      .andExpect(jsonPath("@.dependencies[0].datasources").isArray())
      .andExpect(jsonPath("@.dependencies[0].datasources[0]").value("foo:42/bar"))
      .andExpect(jsonPath("@.dependencies[0].url").doesNotExist())
      .andExpect(jsonPath("@.dependencies[0].name").value("test"))
      .andExpect(jsonPath("@.dependencies[0].type").value("db"))
      .andExpect(jsonPath("@.dependencies[0].subtype").value("MongoDB"))
      .andExpect(jsonPath("@.dependencies[1].url").value("foobar:4711"))
      .andExpect(jsonPath("@.dependencies[1].type").value("service"))
      .andExpect(jsonPath("@.dependencies[1].subtype").value("REST"))
      .andExpect(jsonPath("@.dependencies[1].datasources").doesNotExist());
  }

  @Test
  public void shouldReturnCriticalityAndExpectations() throws Exception {
    when(externalDependencies.getDependencies()).thenReturn(
      asList(mongoDependency(singletonList(datasource("foo:42/bar"))).build(),
        restServiceDependency("foobar:4711").withCriticality(criticality(HIGH, "Bad. Really bad."))
          .withExpectations(lowExpectations())
          .build()));
    mockMvc.perform(get("/internal/dependencies").accept(APPLICATION_JSON))
      .andExpect(jsonPath("@.dependencies[0].criticality.level").value("NOT_SPECIFIED"))
      .andExpect(jsonPath("@.dependencies[0].criticality.disasterImpact").value("Not Specified"))
      .andExpect(jsonPath("@.dependencies[0].expectations.availability").value("NOT_SPECIFIED"))
      .andExpect(jsonPath("@.dependencies[0].expectations.performance").value("NOT_SPECIFIED"))
      .andExpect(jsonPath("@.dependencies[1].criticality.level").value("HIGH"))
      .andExpect(jsonPath("@.dependencies[1].criticality.disasterImpact").value("Bad. Really bad."))
      .andExpect(jsonPath("@.dependencies[1].expectations.availability").value("LOW"))
      .andExpect(jsonPath("@.dependencies[1].expectations.performance").value("LOW"));
  }

  @SpringBootApplication(scanBasePackages = "com.breuninger.boot.status")
  public static class TestServer {

    public static void main(final String... args) {
      run(TestServer.class, args);
    }
  }
}
