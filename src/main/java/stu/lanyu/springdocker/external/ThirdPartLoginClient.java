package stu.lanyu.springdocker.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import stu.lanyu.springdocker.fallback.ThirdPartLoginFallback;

@FeignClient(name = "ThirdPartLogin", url="http://139.196.96.120:8087/login",
    fallback = ThirdPartLoginFallback.class)
public interface ThirdPartLoginClient {

    @PostMapping(value = "/Admin/api/user/login")
    boolean login(String userName, String password);
}
