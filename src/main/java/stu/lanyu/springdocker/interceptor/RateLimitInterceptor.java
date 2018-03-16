package stu.lanyu.springdocker.interceptor;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import stu.lanyu.springdocker.annotation.RateLimiter;
import stu.lanyu.springdocker.redis.RedisRateLimiter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    private RateLimiter preHandleAnnotation(Object handler) {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
            return rateLimiter;
        }else {
            return null;
        }
    }

    @Autowired
    @Qualifier("RedisSubscriberConnectionFactory")
    private RedisClient redis;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        boolean isOverLimit = false;
        RateLimiter rateLimiter = preHandleAnnotation(handler);

        if (rateLimiter != null) {

            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();

            int qps = rateLimiter.qps();
            int interval = rateLimiter.interval();
            int minFrequencyInInterval = rateLimiter.minFrequencyInInterval();

            StatefulRedisConnection<String, String> connection = redis.connect();

            try {

                if (RedisRateLimiter.acquireToken(connection, method.getName(), qps, interval, minFrequencyInInterval)) {
                    response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "单位时间请求过多");
                    isOverLimit = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.close();
            }

            return !isOverLimit;
        }

        return true;
    }
}
