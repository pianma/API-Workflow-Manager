package com.apiworkflow.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public abstract class SelfValidating<T> {

  private final Validator validator;

  public SelfValidating() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    this.validator = factory.getValidator();
  }

  /**
   * Evaluates all Bean Validations on the attributes of this
   * instance.
   */
  protected void validateSelf() {
    @SuppressWarnings("unchecked")
    T self = (T) this;
    Set<ConstraintViolation<T>> violations = validator.validate(self);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
