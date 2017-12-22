package stu.lanyu.springdocker.interceptor;

import io.jsonwebtoken.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import stu.lanyu.springdocker.annotation.Approve;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.utility.JWTUtility;
import stu.lanyu.springdocker.utility.StringUtility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class ApproveInterceptor extends HandlerInterceptorAdapter {

    private Approve preHandleAnnotation(Object handler) {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            Approve approve = method.getAnnotation(Approve.class);
            return approve;
        }else {
            return null;
        }
    }

//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                           ModelAndView modelAndView) throws Exception {
//        if (response.getStatus() == 500) {
//            modelAndView.setViewName("/error/500");
//        } else if (response.getStatus() == 404) {
//            modelAndView.setViewName("/error/404");
//        } else {
//            super.postHandle(request, response, handler, modelAndView);
//        }
//    }

    private Jws<Claims> judgeRefreshToken(HttpServletRequest request, HttpServletResponse
                                      response, Object handler) {
        Jws<Claims> claimsJws = null;
        String refreshToken = request
                .getHeader(GlobalConfig.WebConfig.HEADER_REFRESHTOKEN);

        if (!StringUtility.isNullOrEmpty(refreshToken)) {
            try {
                claimsJws = JWTUtility.parseRefreshJWT(refreshToken);
            } catch (ExpiredJwtException e) { e.printStackTrace(); }
            catch (UnsupportedJwtException e) { e.printStackTrace(); }
            catch (MalformedJwtException e) { e.printStackTrace(); }
            catch (SignatureException e) { e.printStackTrace(); }
            catch (IllegalArgumentException e) { e.printStackTrace(); }
        }

        return claimsJws;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Approve approve = preHandleAnnotation(handler);

        if (approve != null) {
            boolean tokenValid = false;
            String accessToken = request
                    .getHeader(GlobalConfig.WebConfig.HEADER_AUTHORIZE);

            if (!StringUtility.isNullOrEmpty(accessToken)) {
                try {
                    Jws<Claims> claims = JWTUtility.parseJWT(accessToken);

                    String role = claims.getHeader()
                            .get(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY).toString();

                    if (approve.role().equals(role)) {

                        tokenValid = true;

                        request.setAttribute(GlobalConfig.WebConfig.CLAIMS_USER_KEY,
                                claims.getHeader()
                                        .get(GlobalConfig.WebConfig.CLAIMS_USER_KEY)
                        );

                        request.setAttribute(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY,
                                role
                        );
                    }
                } catch (ExpiredJwtException e) { }
                catch (UnsupportedJwtException e) { }
                catch (MalformedJwtException e) { }
                catch (SignatureException e) { }
                catch (IllegalArgumentException e) { }

                if (!tokenValid)
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
            }
            else {
                Jws<Claims> claimsJws = judgeRefreshToken(request, response, handler);
                if (claimsJws == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
                } else {
                    response.setHeader(GlobalConfig.WebConfig.HEADER_REFRESHTOKEN,
                            JWTUtility.createJWT(GlobalConfig.JWTConfig.JWTREFRESHID,
                                    Long.parseLong(claimsJws.getHeader().get(GlobalConfig.WebConfig.CLAIMS_USER_KEY).toString()),
                                    claimsJws.getHeader().get(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY).toString(),
                                    GlobalConfig.JWTConfig.TTLMILLIS));
                    tokenValid = true;
                }
            }

            return tokenValid;
        }

        return true;
    }
}
