package it.moneyverse.test.utils.helper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import org.springframework.util.ReflectionUtils;

public class MapperTestHelper {

    private MapperTestHelper() {}

    public static <T, U> List<T> map(List<U> source, Class<T> targetClass) {
        return source.stream()
            .map(entity -> MapperTestHelper.map(entity, targetClass))
            .toList();
    }

    public static <T, U> T map(U source, Class<T> targetClass) {
        try {
            T target =  targetClass.getDeclaredConstructor().newInstance();
            Class<?> sourceClass = source.getClass();
            Class<?> targetClassHierarchy = targetClass;
            while (sourceClass != null && targetClassHierarchy != null) {
                mapFields(source, target, sourceClass, targetClassHierarchy);
                sourceClass = sourceClass.getSuperclass();
                targetClassHierarchy = targetClassHierarchy.getSuperclass();
            }
            return target;
        } catch (Exception e) {
            throw new IllegalStateException("Mapping failed: ", e);
        }
    }

    private static void mapFields(Object source, Object target, Class<?> sourceClass, Class<?> targetClass) throws IllegalAccessException {
        Field[] sourceFields = sourceClass.getDeclaredFields();
        Field[] targetFields = targetClass.getDeclaredFields();
        for (Field sourceField : sourceFields) {
            ReflectionUtils.makeAccessible(sourceField);
            for (Field targetField : targetFields) {
                ReflectionUtils.makeAccessible(targetField);
                if (Objects.equals(sourceField.getName(), targetField.getName()) && Objects.equals(sourceField.getType(), targetField.getType())) {
                    ReflectionUtils.setField(targetField, target, sourceField.get(source));
                }
            }
        }
    }
}
