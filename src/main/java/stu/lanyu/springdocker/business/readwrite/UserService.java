package stu.lanyu.springdocker.business.readwrite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.repository.readwrite.UserRepository;

@Service("UserServiceReadwrite")
public class UserService {

    @Autowired(required = true)
    @Qualifier("UserRepositoryReadwrite")
    private UserRepository userRepository;

    public void register(User user) {
        userRepository.save(user);
    }
}
