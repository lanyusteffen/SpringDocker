package stu.lanyu.springdocker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import stu.lanyu.springdocker.business.readwrite.UserService;
import stu.lanyu.springdocker.config.GlobalConfig;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.exception.ValidationException;
import stu.lanyu.springdocker.request.LoginRequest;
import stu.lanyu.springdocker.request.RegisterRequest;

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
    public boolean Register(@RequestBody RegisterRequest registerRequest)
            throws ValidationException {
        try {
            registerRequest.Validation();
        }
        catch (ValidationException ve) {
            throw ve;
        }

        User user = registerRequest.mapToDomain();

        if (this.userQueryService.isExistedUserByName(user.getName())) {
            throw new ValidationException("已包含昵称为'" + user.getName() + "'用户");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        if (redisTemplate.opsForZSet().add(GlobalConfig.Redis.REGISTER_NEWUSER_CACHE_KEY, user, timestamp)) {
            this.userService.register(user);
        }
        else {
            return false;
        }

        return true;
    }

    @RequestMapping(value = "/newRegister", method = RequestMethod.GET)
    public @ResponseBody List<User> getLastRegisterUser(int lastCount) {
        Set<User> users = redisTemplate.opsForZSet().range(GlobalConfig.Redis.REGISTER_NEWUSER_CACHE_KEY, 0, -1);
        List<User> userList = new ArrayList<User>();
        userList.addAll(users);
        return userList;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public boolean login(@RequestBody LoginRequest loginRequest) throws ValidationException {
        try {
            loginRequest.Validation();
        }
        catch (ValidationException ve) {
            throw ve;
        }
        return this.userQueryService.login(loginRequest.getPassport(), loginRequest.getPassword());
    }
}
