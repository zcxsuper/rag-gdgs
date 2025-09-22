package com.example.config;

import com.example.interceptor.UserInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

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

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // WebMvcConfigurer.super.configureMessageConverters(converters);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        // Long 转 String
        SimpleModule longToString = new SimpleModule();
        longToString.addSerializer(Long.class, new ToStringSerializer());
        longToString.addSerializer(Long.TYPE, new ToStringSerializer());
        longToString.addDeserializer(Long.class, new CustomLongDeserializer());
        objectMapper.registerModule(longToString);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter);
    }
}
