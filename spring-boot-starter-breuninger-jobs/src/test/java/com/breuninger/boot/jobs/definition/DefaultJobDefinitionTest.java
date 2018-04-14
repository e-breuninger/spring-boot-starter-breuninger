package com.breuninger.boot.jobs.definition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.validateCron;

import org.hamcrest.Matchers;
import org.junit.Test;

public class DefaultJobDefinitionTest {

  @Test
  public void shouldNotFailOnValidCron() {
    validateCron("* * * * * *");
    validateCron("* 1,2,3 * * * *");
    validateCron("* * * * * ?");
    validateCron("* * * * * 0");
    validateCron("* * 21 * * *");
  }

  @Test
  public void shouldFailOnInvalidInput() {
    checkFailure("");
    checkFailure(" ");
    checkFailure("* * * * * * *");
    checkFailure("99 0 0 0 0 0 ");
    checkFailure("* * * * *");
    checkFailure("46-66 0 0 0 * *");
  }

  private void checkFailure(final String cron) {
    try {
      validateCron(cron);
      fail("'" + cron + "' should yield an IllegalArgumentException");
    } catch (final Exception e) {
      assertThat(e, Matchers.instanceOf(IllegalArgumentException.class));
    }
  }
}
