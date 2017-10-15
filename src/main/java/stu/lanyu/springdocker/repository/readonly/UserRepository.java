package stu.lanyu.springdocker.repository.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadOnly;
import stu.lanyu.springdocker.domain.User;

@Repository("UserRepositoryReadonly")
@ReadOnly
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    User findOneByPassportAndPassword(String passport, String password);
    User findOneByName(String name);
}
