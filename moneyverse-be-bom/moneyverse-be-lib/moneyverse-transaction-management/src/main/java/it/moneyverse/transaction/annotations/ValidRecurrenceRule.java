package it.moneyverse.transaction.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import it.moneyverse.transaction.model.validator.RecurrenceRuleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = RecurrenceRuleValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidRecurrenceRule {
  String message() default "Invalid recurrence rule";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
