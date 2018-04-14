package com.breuninger.boot.validation.validators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class EnumValidatorTest {

  @Test
  public void shouldValidateSuccessfully() {
    final var enumValidator = createAndInitializeValidator(false, false);
    final var valid = enumValidator.isValid("foo", null);
    assertThat(valid, is(true));
  }

  @Test
  public void shouldFailForInvalidValue() {
    final var enumValidator = createAndInitializeValidator(false, false);
    final var valid = enumValidator.isValid("xxx", null);
    assertThat(valid, is(false));
  }

  @Test
  public void shouldFailWhenNotIgnoringCase() {
    final var enumValidator = createAndInitializeValidator(false, false);
    final var valid = enumValidator.isValid("Foo", null);
    assertThat(valid, is(false));
  }

  @Test
  public void shouldSucceedWhenIgnoringCase() {
    final var enumValidator = createAndInitializeValidator(true, false);
    final var valid = enumValidator.isValid("Foo", null);
    assertThat(valid, is(true));
  }

  @Test
  public void shouldFailForNull() {
    final var enumValidator = createAndInitializeValidator(true, false);
    final var valid = enumValidator.isValid(null, null);
    assertThat(valid, is(false));
  }

  @Test
  public void shouldAllowNullWhenFlagIsSet() {
    final var enumValidator = createAndInitializeValidator(true, true);
    final var valid = enumValidator.isValid(null, null);
    assertThat(valid, is(true));
  }

  @Test
  public void shouldFailForEmptyString() {
    final var enumValidator = createAndInitializeValidator(true, false);
    final var valid = enumValidator.isValid("", null);
    assertThat(valid, is(false));
  }

  private EnumValidator createAndInitializeValidator(final boolean ignoreCase, final boolean allowNull) {
    final var enumValidator = new EnumValidator();
    enumValidator.initialize(createAnnotation(TestEnum.class, ignoreCase, allowNull));
    return enumValidator;
  }

  private IsEnum createAnnotation(final Class<TestEnum> myEnum, final boolean ignoreCase, final boolean allowNull) {
    final var mockAnnotation = mock(IsEnum.class);
    when(mockAnnotation.enumClass()).thenReturn((Class) myEnum);
    when(mockAnnotation.ignoreCase()).thenReturn(ignoreCase);
    when(mockAnnotation.allowNull()).thenReturn(allowNull);

    return mockAnnotation;
  }

  enum TestEnum {
    foo, bar
  }
}
