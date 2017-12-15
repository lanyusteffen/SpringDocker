package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.repository.readonly.UserRepository;

@Service("UserServiceReadonly")
public class UserService {

    @Autowired(required = true)
    @Qualifier("UserRepositoryReadonly")
    private UserRepository userRepository;

    public User findUserByPassport(String passport) {
        return userRepository.findOneByPassport(passport);
    }

    public boolean login(String passport, String password) {

        User user = userRepository.findOneByPassportAndPassword(passport, password);

        if (user == null)
            return false;

        return true;
    }
}
