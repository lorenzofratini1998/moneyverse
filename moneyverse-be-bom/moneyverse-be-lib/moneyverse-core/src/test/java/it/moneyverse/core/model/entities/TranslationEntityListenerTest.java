package it.moneyverse.core.model.entities;

import it.moneyverse.core.model.context.LocaleContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TranslationEntityListenerTest {
    private TranslationEntityListener listener;

    @BeforeEach
    void setUp() {
        listener = new TranslationEntityListener();
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.clear();
    }

    @Test
    void postLoad_appliesTranslationsForTranslatableEntity() {
        Translatable entity = mock(Translatable.class);
        LocaleContextHolder.setLocale("it");

        listener.postLoad(entity);

        verify(entity, times(1)).applyTranslations("it");
    }

    @Test
    void postLoad_doesNothingForNonTranslatableEntity() {
        Object entity = new Object();
        LocaleContextHolder.setLocale("fr");

        listener.postLoad(entity);
    }

    @Test
    void postLoad_usesDefaultLocaleWhenNotSet() {
        Translatable entity = mock(Translatable.class);

        listener.postLoad(entity);

        verify(entity, times(1)).applyTranslations("en");
    }
}
