package com.breuninger.boot.jobs.controller;

import static java.time.Duration.ofHours;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.fixedDelayJobDefinition;
import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.manuallyTriggerableJobDefinition;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.navigation.NavBar;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.service.JobDefinitionService;
import com.breuninger.boot.jobs.service.JobMetaService;

public class JobDefinitionsControllerTest {

  private static final String MANAGEMENT_CONTEXT = "/someManagementContext";
  private final BreuningerApplicationProperties webEndpointProperties = new BreuningerApplicationProperties();
  @Mock
  private NavBar navBar;
  @Mock
  private JobDefinitionService jobDefinitionService;
  @Mock
  private JobMetaService jobMetaService;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    initMocks(this);
    webEndpointProperties.getManagement().setBasePath(MANAGEMENT_CONTEXT);
    final var controller = new JobDefinitionsController(jobDefinitionService, jobMetaService, navBar,
      webEndpointProperties);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .addPlaceholderValue("breuninger.application.management.base-path", MANAGEMENT_CONTEXT)
      .build();
  }

  @Test
  public void shouldReturn404IfJobDefinitionIsUnknown() throws Exception {
    when(jobDefinitionService.getJobDefinition("FooJob")).thenReturn(empty());
    mockMvc.perform(get(MANAGEMENT_CONTEXT + "/jobdefinitions/FooJob")).andExpect(status().is(404));
  }

  @Test
  public void shouldReturnJobDefinitionIfJobExists() throws Exception {
    // given
    final var jobType = "FooJob";
    final var expectedDef = jobDefinition(jobType, "Foo");
    when(jobDefinitionService.getJobDefinition(jobType)).thenReturn(Optional.of(expectedDef));

    // when
    mockMvc.perform(get(MANAGEMENT_CONTEXT + "/jobdefinitions/FooJob").accept("application/json"))
      .andExpect(status().is(200))
      .andExpect(content().json(
        "{\n" + "  \"type\": \"FooJob\",\n" + "  \"name\": \"Foo\",\n" + "  \"retries\": 0,\n" + "  \"fixedDelay\": 3600,\n" +
          "  \"links\": [\n" + "    {\n" + "      \"href\": \"" + MANAGEMENT_CONTEXT + "/jobsdefinitions/FooJob\",\n" +
          "      \"rel\": \"self\"\n" + "    },\n" + "    {\n" + "      \"href\": \"" + MANAGEMENT_CONTEXT +
          "/jobdefinitions\",\n" + "      \"rel\": \"collection\"\n" + "    },\n" + "    {\n" + "      \"href\": \"" +
          MANAGEMENT_CONTEXT + "/jobs/FooJob\",\n" +
          "      \"rel\": \"http://github.com/e-breuninger/spring-boot-starter-breuninger/link-relations/job/trigger\"\n" + "    }\n" + "  ]\n" + "}"));
  }

  @Test
  public void shouldReturnAllJobDefinitions() throws Exception {
    // given
    final var fooJobDef = jobDefinition("FooJob", "Foo");
    final var barJobDef = jobDefinition("BarJob", "Bar");
    when(jobDefinitionService.getJobDefinitions()).thenReturn(asList(fooJobDef, barJobDef));

    // when
    mockMvc.perform(get(MANAGEMENT_CONTEXT + "/jobdefinitions/").accept("application/json"))
      .andExpect(status().is(200))
      .andExpect(content().json(
        "{\n" + "  \"links\": [\n" + "    {\n" + "      \"href\": \"" + MANAGEMENT_CONTEXT + "/jobdefinitions/FooJob\",\n" +
          "      \"rel\": \"http://github.com/e-breuninger/spring-boot-starter-breuninger/link-relations/job/definition\",\n" +
          "      \"title\": \"Foo\"\n" + "    },\n" + "    {\n" + "      \"href\": \"" + MANAGEMENT_CONTEXT +
          "/jobdefinitions/BarJob\",\n" +
          "      \"rel\": \"http://github.com/e-breuninger/spring-boot-starter-breuninger/link-relations/job/definition\",\n" +
          "      \"title\": \"Bar\"\n" + "    },\n" + "    {\n" + "      \"href\": \"" + MANAGEMENT_CONTEXT +
          "/jobdefinitions\",\n" + "      \"rel\": \"self\",\n" + "      \"title\": \"Self\"\n" + "    }\n" + "  ]\n" + "}"));
  }

  @Test
  public void shouldReturnAllJobDefinitionsAsHtml() throws Exception {
    // given
    final var fooJobDef = jobDefinition("FooJob", "Foo");
    final var barJobDef = notTriggerableDefinition("BarJob", "Bar");
    when(jobDefinitionService.getJobDefinitions()).thenReturn(asList(fooJobDef, barJobDef));

    // when
    mockMvc.perform(get(MANAGEMENT_CONTEXT + "/jobdefinitions/").accept("text/html"))
      .andExpect(status().is(200))
      .andDo(result -> {
        final var model = result.getModelAndView().getModel();
        final var jobDefinitions = (List<Map<String, Object>>) model.get("jobdefinitions");
        assertThat(jobDefinitions.size(), is(2));
        assertThat(jobDefinitions.get(0).get("frequency"), is("Every 60 Minutes"));
        assertThat(jobDefinitions.get(0).get("isDisabled"), is(false));
        assertThat(jobDefinitions.get(0).get("comment"), is(""));
        assertThat(jobDefinitions.get(1).get("frequency"), is("Never"));
        assertThat(jobDefinitions.get(1).get("isDisabled"), is(false));
        assertThat(jobDefinitions.get(1).get("comment"), is(""));
      });
  }

  @Test
  public void shouldConvertToSecondsIfSecondsIsLessThan60() throws Exception {
    // given
    final var jobDef = jobDefinition("TheJob", "Job", ofSeconds(59));
    when(jobDefinitionService.getJobDefinitions()).thenReturn(singletonList(jobDef));

    // when
    mockMvc.perform(get(MANAGEMENT_CONTEXT + "/jobdefinitions/").accept("text/html"))
      .andExpect(status().is(200))
      .andDo(result -> {
        final var jobDefinitions = (List<Map<String, Object>>) result.getModelAndView()
          .getModel()
          .get("jobdefinitions");
        assertThat(jobDefinitions.size(), is(1));
        assertThat(jobDefinitions.get(0).get("frequency"), is("Every 59 Seconds"));
      });
  }

  @Test
  public void shouldConvertToMinutesIfSecondsIsNotLessThan60() throws Exception {
    // given
    final var jobDef = jobDefinition("TheJob", "Job", ofSeconds(60));
    when(jobDefinitionService.getJobDefinitions()).thenReturn(singletonList(jobDef));

    // when
    mockMvc.perform(get(MANAGEMENT_CONTEXT + "/jobdefinitions/").accept("text/html"))
      .andExpect(status().is(200))
      .andDo(result -> {
        final var jobDefinitions = (List<Map<String, Object>>) result.getModelAndView()
          .getModel()
          .get("jobdefinitions");
        assertThat(jobDefinitions.size(), is(1));
        assertThat(jobDefinitions.get(0).get("frequency"), is("Every 1 Minutes"));
      });
  }

  private JobDefinition jobDefinition(final String jobType, final String name) {
    return jobDefinition(jobType, name, ofHours(1));
  }

  private JobDefinition jobDefinition(final String jobType, final String name, final Duration fixedDelay) {
    return fixedDelayJobDefinition(jobType, name, name, fixedDelay, 0, empty());
  }

  private JobDefinition notTriggerableDefinition(final String jobType, final String name) {
    return manuallyTriggerableJobDefinition(jobType, name, name, 0, empty());
  }
}
