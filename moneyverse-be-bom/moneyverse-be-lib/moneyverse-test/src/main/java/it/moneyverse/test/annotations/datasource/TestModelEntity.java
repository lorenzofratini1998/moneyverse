package it.moneyverse.test.annotations.datasource;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestModelEntity {}
