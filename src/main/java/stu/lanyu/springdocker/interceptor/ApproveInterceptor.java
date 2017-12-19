package stu.lanyu.springdocker.interceptor;

import io.jsonwebtoken.Claims;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import stu.lanyu.springdocker.annotation.Approve;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.utility.JWTUtility;
import stu.lanyu.springdocker.utility.StringUtility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class ApproveInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Approve approve = method.getAnnotation(Approve.class);

        if (approve != null) {
            String accessToken = request.getHeader(GlobalConfig.WebConfig.HEADER_AUTHORIZE);

            if (!StringUtility.isNullOrEmpty(accessToken)) {
                Claims claims = JWTUtility.parseJWT(accessToken);
                request.setAttribute(GlobalConfig.WebConfig.CLAIMS_USER_KEY,
                        claims.get(GlobalConfig.WebConfig.CLAIMS_USER_KEY, Integer.class).toString());
                request.setAttribute(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY, claims.get(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY, String.class));
            }

            return false;
        }

        return true;
    }
}
