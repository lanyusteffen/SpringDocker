package stu.lanyu.springdocker.business.readonly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import stu.lanyu.springdocker.domain.User;
import stu.lanyu.springdocker.exception.DomainException;
import stu.lanyu.springdocker.repository.readonly.UserRepository;
import stu.lanyu.springdocker.response.ValidationError;
import stu.lanyu.springdocker.response.ValidationErrors;

@Service("UserServiceReadonly")
public class UserService {

    @Autowired(required = true)
    @Qualifier("UserRepositoryReadonly")
    private UserRepository userRepository;

    public User findUserByPassport(String passport) {
        return userRepository.findOneByPassport(passport);
    }

    public boolean login(String passport, String password) throws DomainException {

        User user = userRepository.findOneByPassportAndPassword(passport, password);

        if (user == null) {
            ValidationErrors errors = new ValidationErrors();
            errors.getErrorItems().add(new ValidationError("Password", null));
            throw new DomainException("密码不正确!", errors);
        }

        if (!user.isAuditToUse()) {
            ValidationErrors errors = new ValidationErrors();
            errors.getErrorItems().add(new ValidationError("IsAuditToUse", null));
            throw new DomainException("账户'" + passport + "'未授权进行登录!", errors);
        }

        return user.isAuditToUse();
    }
}
