package stu.lanyu.springdocker.exception;

import stu.lanyu.springdocker.response.ValidationErrors;

public class DomainException extends Exception {

    private ValidationErrors validationErrors;

    public DomainException() {
        super();
        this.validationErrors = new ValidationErrors();
    }

    public DomainException(String message) {
        super(message);
        this.validationErrors = new ValidationErrors();
    }

    public DomainException(String message, ValidationErrors validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public DomainException(ValidationErrors validationErrors) {
        super();
        this.validationErrors = validationErrors;
    }

    public ValidationErrors getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(ValidationErrors validationErrors) {
        this.validationErrors = validationErrors;
    }
}