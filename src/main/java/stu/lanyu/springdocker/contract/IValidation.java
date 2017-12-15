package stu.lanyu.springdocker.contract;

import stu.lanyu.springdocker.exception.DomainException;

public interface IValidation {
    void validation() throws DomainException;
}
