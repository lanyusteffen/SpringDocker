package stu.lanyu.springdocker.interceptor;

import io.jsonwebtoken.*;
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
                                      response) {
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

    private Jws<Claims> judgeAccessToken(HttpServletRequest request, HttpServletResponse
            response)  {

        Jws<Claims> claimsJws = null;
        String accessToken = request
                .getHeader(GlobalConfig.WebConfig.HEADER_AUTHORIZE);

        if (!StringUtility.isNullOrEmpty(accessToken)) {
            try {
                claimsJws = JWTUtility.parseJWT(accessToken);
            } catch (ExpiredJwtException e) { e.printStackTrace(); }
            catch (UnsupportedJwtException e) { e.printStackTrace(); }
            catch (MalformedJwtException e) { e.printStackTrace(); }
            catch (SignatureException e) { e.printStackTrace(); }
            catch (IllegalArgumentException e) { e.printStackTrace(); }
        }

        return claimsJws;
    }

    private boolean judgeApprove(Approve approve, Jws<Claims> claimsJws, HttpServletRequest request, HttpServletResponse
            response) {

        boolean tokenValid = false;

        String role = claimsJws.getHeader()
                .get(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY).toString();

        if (approve.role().equals(role)) {

            request.setAttribute(GlobalConfig.WebConfig.CLAIMS_USER_KEY,
                    claimsJws.getHeader().get(GlobalConfig.WebConfig.CLAIMS_USER_KEY));

            request.setAttribute(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY, role);

            tokenValid = true;
        }

        return tokenValid;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Approve approve = preHandleAnnotation(handler);

        // 无需验证权限API
        if (approve == null) {
            return true;
        }

        Jws<Claims> claimsJws = judgeAccessToken(request, response);
        boolean tokenValid = true;

        if (claimsJws == null) {
            // 验证RefreshToken
            claimsJws = judgeRefreshToken(request, response);
            if (claimsJws == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
                tokenValid = false;
            } else {
                if (judgeApprove(approve, claimsJws, request, response)) {
                    // RefreshToken验证通过需要将更新得AccessToken验证给客户端
                    response.setHeader(GlobalConfig.WebConfig.HEADER_REFRESHTOKEN,
                            JWTUtility.createJWT(GlobalConfig.JWTConfig.JWTID,
                                    Long.parseLong(claimsJws.getHeader().get(GlobalConfig.WebConfig.CLAIMS_USER_KEY).toString()),
                                    claimsJws.getHeader().get(GlobalConfig.WebConfig.CLAIMS_ROLE_KEY).toString(),
                                    GlobalConfig.JWTConfig.TTLMILLIS));
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
                    tokenValid = false;
                }
            }
        } else {
            if (!judgeApprove(approve, claimsJws, request, response)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
                tokenValid = false;
            }
        }

        return tokenValid;
    }
}
