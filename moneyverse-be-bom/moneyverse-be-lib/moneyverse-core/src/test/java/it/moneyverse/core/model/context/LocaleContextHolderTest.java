package it.moneyverse.core.model.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocaleContextHolderTest {
    @AfterEach
    void tearDown() {
        LocaleContextHolder.clear();
    }

    @Test
    void testSetAndGetLocale() {
        LocaleContextHolder.setLocale("it");
        assertEquals("it", LocaleContextHolder.getLocale(), "Locale should be 'it'");
    }

    @Test
    void testGetLocaleDefaultsToEnWhenNotSet() {
        assertEquals("en", LocaleContextHolder.getLocale(), "Locale should default to 'en'");
    }

    @Test
    void testClearRemovesLocale() {
        LocaleContextHolder.setLocale("fr");
        assertEquals("fr", LocaleContextHolder.getLocale());
        LocaleContextHolder.clear();
        assertEquals("en", LocaleContextHolder.getLocale(), "After clear, locale should default to 'en'");
    }

    @Test
    void testSetMultipleLocalesInDifferentThreads() throws InterruptedException {
        Thread t1 = new Thread(() -> LocaleContextHolder.setLocale("it"));
        Thread t2 = new Thread(() -> LocaleContextHolder.setLocale("fr"));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertEquals("en", LocaleContextHolder.getLocale(), "Main thread should still default to 'en'");
    }
}
