package stu.lanyu.springdocker.contract;

import stu.lanyu.springdocker.exception.ValidationException;

public interface IValidation {
    void Validation() throws ValidationException;
}
