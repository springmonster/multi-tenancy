package com.example.springbootmysqljooq.config;

import com.example.multitenancylibrary.network.MultiTenancyStorage;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String tenantID = request.getHeader("X-TenantID");
                if (null == tenantID) {
                    throw new RuntimeException("Tenant ID must not be null!");
                }
                MultiTenancyStorage.setTenantID(Integer.valueOf(tenantID));
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                MultiTenancyStorage.setTenantID(null);
            }
        });
    }
}
