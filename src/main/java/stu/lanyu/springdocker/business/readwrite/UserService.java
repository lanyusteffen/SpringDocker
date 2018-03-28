package stu.lanyu.springdocker.business.readwrite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.business.AbstractBusinessService;
import stu.lanyu.springdocker.domain.entity.User;
import stu.lanyu.springdocker.repository.readwrite.UserRepository;

@Service("UserServiceReadwrite")
public class UserService extends AbstractBusinessService {

    @Autowired(required = true)
    @Qualifier("UserRepositoryReadwrite")
    private UserRepository userRepository;

    public void register(User user) {
        userRepository.save(user);
    }
}
