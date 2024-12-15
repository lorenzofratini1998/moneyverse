package it.moneyverse.core.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JsonUtilsTest {

  @Test
  void testToJson() {
    Person person = new Person();
    person.setFirstName("John");
    person.setLastName("Doe");
    person.setAge(30);

    String json = JsonUtils.toJson(person);

    assertNotNull(json);
    assertTrue(json.contains("John"));
    assertTrue(json.contains("Doe"));
    assertTrue(json.contains("30"));
  }

  @Test
  void testFromJson() {
    String json = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":30}";

    Person person = JsonUtils.fromJson(json, Person.class);

    assertNotNull(person);
    assertEquals("John", person.getFirstName());
    assertEquals("Doe", person.getLastName());
    assertEquals(30, person.getAge());
  }

  @Test
  void testFromJson_WithInvalidJson() {
    String invalidJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":}";

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          JsonUtils.fromJson(invalidJson, Person.class);
        });
  }

  // Person class used for serialization and deserialization
  public static class Person {
    private String firstName;
    private String lastName;
    private int age;

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }
  }
}
