package stu.lanyu.springdocker.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import redis.clients.jedis.Jedis;
import stu.lanyu.springdocker.annotation.RateLimiter;
import stu.lanyu.springdocker.redis.RedisRateLimiter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    @Qualifier("RedisSubscriberConnectionFactory")
    private JedisConnectionFactory jedisConnectionFactory;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);

        if (rateLimiter != null) {
            int limit = rateLimiter.limit();
            int timeout = rateLimiter.timeout();
            Jedis jedis = jedisConnectionFactory.getShardInfo().createResource();
            String token = RedisRateLimiter.acquireTokenFromBucket(jedis, method.getName(), limit, timeout);
            if (token == null) {
                response.sendError(500);
                return false;
            }
            jedis.close();
        }

        return true;
    }
}
