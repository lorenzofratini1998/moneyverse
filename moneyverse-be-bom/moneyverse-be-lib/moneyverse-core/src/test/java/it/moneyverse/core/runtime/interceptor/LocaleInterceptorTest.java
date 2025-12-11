package it.moneyverse.core.runtime.interceptor;

import it.moneyverse.core.model.context.LocaleContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

public class LocaleInterceptorTest {
    private LocaleInterceptor interceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new LocaleInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void preHandle_setsLocale_whenHeaderPresent() {
        request.addHeader("Accept-Language", "it");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals("it", LocaleContextHolder.getLocale());
    }

    @Test
    void preHandle_defaultsToEn_whenHeaderMissing() {
        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals("en", LocaleContextHolder.getLocale());
    }

    @Test
    void afterCompletion_clearsLocale() {
        request.addHeader("Accept-Language", "it");
        interceptor.preHandle(request, response, new Object());

        interceptor.afterCompletion(request, response, new Object(), null);

        assertEquals("en", LocaleContextHolder.getLocale());
    }

}
