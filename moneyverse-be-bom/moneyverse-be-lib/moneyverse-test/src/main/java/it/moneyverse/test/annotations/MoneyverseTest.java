package it.moneyverse.test.annotations;

import it.moneyverse.test.annotations.datasource.FlywayTest;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@FlywayTest
@TestPropertySource(
    properties = {
      "logging.level.org.grpcmock.GrpcMock=WARN",
      "logging.level.org.apache.kafka.clients.*=ERROR",
      "logging.level.org.springframework.kafka.listener=ERROR"
    })
public @interface MoneyverseTest {}
