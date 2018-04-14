package com.breuninger.boot.validation.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import static com.jayway.restassured.RestAssured.given;

import java.util.Collections;

import javax.servlet.ServletContext;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.breuninger.boot.validation.web.ValidationExceptionHandlerAcceptanceTest.TestConfiguration;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

import com.breuninger.boot.validation.validators.SafeId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableAutoConfiguration
@ComponentScan("com.breuninger.boot.validation")
@ContextConfiguration(classes = {
  ValidationExceptionHandler.class, TestConfiguration.class
})
public class ValidationExceptionHandlerAcceptanceTest {

  @LocalServerPort
  private int port;

  @Autowired
  private ServletContext servletContext;

  @Before
  public void setUp() {
    RestAssured.port = port;
    RestAssured.basePath = servletContext.getContextPath();
  }

  @Test
  public void shouldValidateAndProduceErrorRepresentation() {
    given().contentType(ContentType.JSON)
      .body("{\"id\":\"_!NON_SAFE_ID!!?**\"}")
      .when()
      .put("/testing")
      .then()
      .assertThat()
      .statusCode(422)
      .and()
      .header("Content-type", Matchers.containsString(";charset=utf-8"))
      .content("errors.id[0].key", Collections.emptyList(), is("id.invalid"))
      .content("errors.id[0].message", Collections.emptyList(), is("Ungueltiger Id-Wert."))
      .content("errors.id[0].rejected", Collections.emptyList(), is("_!NON_SAFE_ID!!?**"));
  }

  static class TestConfiguration {
    @RestController
    public static class TestController {
      @RequestMapping(value = "/testing", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE,
                      produces = APPLICATION_JSON_VALUE)
      public String doTest(@Validated @RequestBody final ContentRepresentation content) {
        return "bla";
      }
    }
  }

  static class ContentRepresentation {
    @SafeId
    private String id;

    public String getId() {
      return id;
    }

    public void setId(final String id) {
      this.id = id;
    }
  }
}

