package org.example.parnasservice.config;

import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestIdInterceptor());
    }

    static class RequestIdInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String requestId = request.getHeader("X-Request-ID");
            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString().replace("-", "");
            }
            MDC.put("requestId", requestId);
            response.setHeader("X-Request-ID", requestId);
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                    Object handler, Exception ex) {
            MDC.remove("requestId");
        }
    }
}
