package stu.lanyu.springdocker.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import stu.lanyu.springdocker.RunnerContext;
import stu.lanyu.springdocker.annotation.Approve;
import stu.lanyu.springdocker.business.readwrite.UserService;
import stu.lanyu.springdocker.config.GlobalAppSettingsProperties;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.exception.DomainException;
import stu.lanyu.springdocker.request.LoginRequest;
import stu.lanyu.springdocker.request.RegisterRequest;
import stu.lanyu.springdocker.response.ApiResponse;
import stu.lanyu.springdocker.response.ValidationError;
import stu.lanyu.springdocker.response.ValidationErrors;
import stu.lanyu.springdocker.security.AESUtils;
import stu.lanyu.springdocker.utility.JWTUtility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Qualifier(value = "UserServiceReadwrite")
    @Autowired(required = true)
    private UserService userService;
    @Qualifier(value = "UserServiceReadonly")
    @Autowired(required = true)
    private stu.lanyu.springdocker.business.readonly.UserService userQueryService;

    @Autowired
    private RedisTemplate<String, User> redisTemplate;

//    @HystrixCommand(fallbackMethod = "registerBreak")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ApiResponse register(@RequestBody RegisterRequest registerRequest) {

        ApiResponse response = ApiResponse.createDomainSuccess();

        try {
            registerRequest.validation();

            User user = registerRequest.mapToDomain();
            registerRequest.makePasswordSecurity(user);

            if (this.userQueryService.findUserByPassport(user.getPassport()) != null) {
                ValidationErrors errors = new ValidationErrors();
                errors.getErrorItems().add(new ValidationError("Passport", null));
                response.setDomainFailure("登录名'" + user.getPassport() + "'已注册", errors);
            }

            if (!response.isValid)
                return response;

            this.userService.register(user);

            if (user.getId() > 0) {
                response.setEntityId(user.getId());
            } else {
                response.setDomainFailure("发生未知异常, 注册失败, 请通知服务提供商", null);
            }

            long timestamp = System.currentTimeMillis() / 1000;
            redisTemplate.opsForZSet().add(GlobalConfig.Redis.REGISTER_NEWUSER_CACHE_KEY, user, timestamp);
        }
        catch (DomainException de) {
            ValidationErrors errors = de.getValidationErrors();
            response.setDomainFailure(de.getMessage(), errors);
        }

        return response;
    }

    @Approve
    @RequestMapping(value = "/getSecurityKey", method = RequestMethod.GET)
    public @ResponseBody List<String> getPrivateKey() {
        List<String> keys = new ArrayList<>();
        GlobalAppSettingsProperties properties = (GlobalAppSettingsProperties)RunnerContext
                .getBean("GlobalAppSettingsProperties");
        switch (properties.getPwdType()) {
            case "AES":
                keys.add(AESUtils.convertAESKeyToString(AESUtils.generateKey()));
                keys.add("");
                break;
            case "RSA":
                keys.add("");
                keys.add("");
                break;
            default:
                break;
        }
        return keys;
    }

    @Approve
    @RequestMapping(value = "/newRegister", method = RequestMethod.GET)
    public @ResponseBody List<User> getLastRegisterUser(int lastCount) {
        Set<User> users = redisTemplate.opsForZSet().range(GlobalConfig.Redis.REGISTER_NEWUSER_CACHE_KEY, 0, -1);
        List<User> userList = new ArrayList<User>();
        userList.addAll(users);
        return userList;
    }

//    @HystrixCommand(fallbackMethod = "loginBreak")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ApiResponse login(@RequestBody LoginRequest loginRequest) {

        ApiResponse response = ApiResponse.createDomainSuccess();

        try {
            loginRequest.validation();

            User user = this.userQueryService.findUserByPassport(loginRequest.getPassport());

            if (user == null) {
                ValidationErrors errors = new ValidationErrors();
                errors.getErrorItems().add(new ValidationError("Passport", null));
                response.setDomainFailure("登陆名'" + loginRequest.getPassport() + "'不存在!", errors);
            }

            if (!response.isValid)
                return response;

            loginRequest.makePasswordSecurity(user);
            loginRequest.setPassword(user.getPassword());

            response.setJudgeResult(this.userQueryService
                    .login(loginRequest.getPassport(), loginRequest.getPassword()));
            response.setEntity(JWTUtility.createAuthToken(user.getId(),
                    GlobalConfig.WebConfig.DEFAULT_ROLE));

            user.setLastLoginTime(new Date());
            user.setLoginTime(user.getLoginTime() + 1);
            this.userService.register(user);
        }
        catch (DomainException de) {
            ValidationErrors errors = de.getValidationErrors();
            response.setDomainFailure(de.getMessage(), errors);
        }

        return response;
    }

    public ApiResponse loginBreak() {
        ValidationErrors errors = new ValidationErrors();
        errors.getErrorItems().add(new ValidationError(null, null));
        ApiResponse breakResponse = ApiResponse.createDomainFailure("登录服务当前不可用", errors);
        return breakResponse;
    }

    public ApiResponse registerBreak() {
        ValidationErrors errors = new ValidationErrors();
        errors.getErrorItems().add(new ValidationError(null, null));
        ApiResponse breakResponse = ApiResponse.createDomainFailure("注册服务当前不可用", errors);
        return breakResponse;
    }
}
