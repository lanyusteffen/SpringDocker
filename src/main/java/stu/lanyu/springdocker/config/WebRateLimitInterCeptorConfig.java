package stu.lanyu.springdocker.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import stu.lanyu.springdocker.interceptor.RateLimitInterceptor;

@Component
public class WebRateLimitInterCeptorConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 注册拦截器
        InterceptorRegistration registration = registry.addInterceptor(new RateLimitInterceptor());

        // 配置拦截的路径
        registration.addPathPatterns("/**");

        // 配置不拦截的路径
        registration.excludePathPatterns("/**.html");
    }
}
