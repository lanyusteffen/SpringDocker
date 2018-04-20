package stu.lanyu.springdocker.fallback;

import stu.lanyu.springdocker.external.ThirdPartLoginClient;

public class ThirdPartLoginFallback implements ThirdPartLoginClient {

    @Override
    public boolean login(String userName, String password) {
        return false;
    }
}
