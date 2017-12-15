package stu.lanyu.springdocker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import stu.lanyu.springdocker.business.readwrite.UserService;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.exception.DomainException;
import stu.lanyu.springdocker.request.LoginRequest;
import stu.lanyu.springdocker.request.RegisterRequest;
import stu.lanyu.springdocker.response.ApiResponse;
import stu.lanyu.springdocker.response.ValidationError;
import stu.lanyu.springdocker.response.ValidationErrors;

import java.util.ArrayList;
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ApiResponse Register(@RequestBody RegisterRequest registerRequest) {

        ApiResponse response = null;

        try {
            registerRequest.validation();

            User user = registerRequest.mapToDomain();

            if (this.userQueryService.findUserByPassport(user.getPassport()) != null) {
                ValidationErrors errors = new ValidationErrors();
                errors.getErrorItems().add(new ValidationError("Passport", null));
                response = ApiResponse.createDomainFailure("登录名'" + user.getPassport() + "'已注册", errors);
            }

            if (response != null && !response.isValid)
                return response;

            this.userService.register(user);

            if (user.getId() > 0) {
                response = ApiResponse.createDomainSuccess(user.getId());
            } else {
                response = ApiResponse.createDomainFailure("发生未知异常, 注册失败, 请通知服务提供商", null);
            }

            long timestamp = System.currentTimeMillis() / 1000;
            redisTemplate.opsForZSet().add(GlobalConfig.Redis.REGISTER_NEWUSER_CACHE_KEY, user, timestamp);
        }
        catch (DomainException de) {
            ValidationErrors errors = de.getValidationErrors();
            response.createDomainFailure(de.getMessage(), errors);
        }

        return response;
    }

    @RequestMapping(value = "/newRegister", method = RequestMethod.GET)
    public @ResponseBody List<User> getLastRegisterUser(int lastCount) {
        Set<User> users = redisTemplate.opsForZSet().range(GlobalConfig.Redis.REGISTER_NEWUSER_CACHE_KEY, 0, -1);
        List<User> userList = new ArrayList<User>();
        userList.addAll(users);
        return userList;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ApiResponse login(@RequestBody LoginRequest loginRequest) {

        ApiResponse response = null;

        try {
            loginRequest.validation();

            User user = this.userQueryService.findUserByPassport(loginRequest.getPassport());

            if (user == null) {
                ValidationErrors errors = new ValidationErrors();
                errors.getErrorItems().add(new ValidationError("Passport", null));
                response = ApiResponse.createDomainFailure("登陆名'" + loginRequest.getPassport() + "'不存在!", errors);
            }

            if (response != null && !response.isValid)
                return response;

            loginRequest.makePasswordSecurity(user);
            loginRequest.setPassword(user.getPassword());

            response = ApiResponse.createDomainSuccess(this.userQueryService.login(loginRequest.getPassport(), loginRequest.getPassword()));
        }
        catch (DomainException de) {
            ValidationErrors errors = de.getValidationErrors();
            response.createDomainFailure(de.getMessage(), errors);
        }

        return response;
    }
}
