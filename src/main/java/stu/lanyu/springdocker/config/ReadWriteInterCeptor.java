package stu.lanyu.springdocker.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import stu.lanyu.springdocker.annotation.Readonly;

@Aspect
@Component
public class ReadWriteInterCeptor implements Ordered {

    @Pointcut(value="execution(public * *(..))")
    public void anyPublicMethod() { }

    @Around("@annotation(readonly)")
    public Object proceed(ProceedingJoinPoint pjp, Readonly readonly) throws Throwable {
        try {

            DbContextHolder.setDbType(DbType.SLAVE);

            Object result = pjp.proceed();

            return result;
        } finally {
            // restore state
            DbContextHolder.clearDbType();
        }
    }

    private int order;

    @Value("1")
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
