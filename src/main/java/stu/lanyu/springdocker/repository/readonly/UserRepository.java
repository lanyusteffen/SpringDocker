package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.Readonly;
import stu.lanyu.springdocker.domain.entity.User;

@Repository("UserRepositoryReadonly")
@Readonly
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    User findOneByPassport(String passport);
    User findOneByPassportAndPassword(String passport, String password);
}
