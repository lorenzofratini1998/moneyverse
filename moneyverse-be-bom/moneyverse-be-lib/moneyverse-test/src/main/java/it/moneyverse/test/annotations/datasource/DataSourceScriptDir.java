package it.moneyverse.test.annotations.datasource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@TempDir(cleanup = CleanupMode.ALWAYS)
public @interface DataSourceScriptDir {

    String fileName();

}
