package stu.lanyu.springdocker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import stu.lanyu.springdocker.interceptor.ApproveInterceptor;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration registration = registry
                .addInterceptor(new ApproveInterceptor());
        registration.addPathPatterns("/**");
        registration.excludePathPatterns("/**.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .exposedHeaders(GlobalConfig.WebConfig.HEADER_AUTHORIZE, GlobalConfig.WebConfig.HEADER_REFRESHTOKEN);
    }
}
