package stu.lanyu.springdocker.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import redis.clients.jedis.Jedis;
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
    private JedisConnectionFactory jedisConnectionFactory;

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

            Jedis jedis = null;

            try {
                jedis = jedisConnectionFactory.getShardInfo().createResource();

                if (RedisRateLimiter.acquireToken(jedis, method.getName(), qps, interval, minFrequencyInInterval)) {
                    response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "单位时间请求过多");
                    isOverLimit = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null)
                    jedis.close();
            }

            return !isOverLimit;
        }

        return true;
    }
}
