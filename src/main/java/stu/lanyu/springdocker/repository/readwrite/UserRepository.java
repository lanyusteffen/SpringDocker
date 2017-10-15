package stu.lanyu.springdocker.repository.readwrite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import stu.lanyu.springdocker.annotation.ReadWrite;
import stu.lanyu.springdocker.domain.User;

@Repository("UserRepositoryReadwrite")
@ReadWrite
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);
}
