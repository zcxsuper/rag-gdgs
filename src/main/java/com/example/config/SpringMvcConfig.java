package com.example.config;

import com.example.interceptor.UserInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("com.example.advice")
@ConditionalOnClass(DispatcherServlet.class) // 保证配置只在 Web 环境 下生效，非 Web 项目不会加载
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    private UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截器会在 Controller 执行前、执行后被调用
        registry.addInterceptor(userInterceptor)
                .excludePathPatterns("/api/v1/user/login")
                .excludePathPatterns("/api/v1/user/register")
                .excludePathPatterns("/api/v1/assistant/chat");
    }
}
