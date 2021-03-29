package com.squadb.workassistantapi.web.config;

import com.squadb.workassistantapi.web.config.auth.LoginMemberIdArgumentResolver;
import com.squadb.workassistantapi.web.interceptor.CheckPermissionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginMemberIdArgumentResolver loginMemberIdArgumentResolver;
    private final CheckPermissionInterceptor checkPermissionInterceptor;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberIdArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(GET.name(), POST.name(), PUT.name(), HEAD.name())
                .allowCredentials(true)
                .allowedOrigins(allowedOrigin);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkPermissionInterceptor);
    }
}
