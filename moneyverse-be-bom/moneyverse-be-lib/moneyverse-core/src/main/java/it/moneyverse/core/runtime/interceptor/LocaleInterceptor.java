package it.moneyverse.core.runtime.interceptor;

import it.moneyverse.core.model.context.LocaleContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LocaleInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String locale = request.getHeader("Accept-Language");
    if (locale != null && !locale.isEmpty()) {
      LocaleContextHolder.setLocale(locale);
    }
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    LocaleContextHolder.clear();
  }
}
