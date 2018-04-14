package com.breuninger.boot.validation.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import cz.jirutka.validator.collection.CommonEachValidator;
import cz.jirutka.validator.collection.constraints.EachConstraint;

@Documented
@EachConstraint(validateAs = SafeId.class)
@Constraint(validatedBy = CommonEachValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EachSafeId {

  String message() default "{invalid.id.value}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
