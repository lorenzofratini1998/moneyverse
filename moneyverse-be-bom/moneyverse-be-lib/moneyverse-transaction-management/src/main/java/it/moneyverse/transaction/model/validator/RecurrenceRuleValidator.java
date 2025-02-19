package it.moneyverse.transaction.model.validator;

import it.moneyverse.transaction.annotations.ValidRecurrenceRule;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.fortuna.ical4j.model.property.RRule;

public class RecurrenceRuleValidator implements ConstraintValidator<ValidRecurrenceRule, String> {

  @Override
  public boolean isValid(String recurrenceRule, ConstraintValidatorContext context) {
    if (recurrenceRule == null || recurrenceRule.isBlank()) {
      return true;
    }
    try {
      new RRule(recurrenceRule);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
