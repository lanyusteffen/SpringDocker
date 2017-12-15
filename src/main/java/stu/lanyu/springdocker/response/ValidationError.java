package stu.lanyu.springdocker.response;

import java.io.Serializable;

public class ValidationError extends AbstractResponse implements Serializable {

    private static final long serialVersionUID = -455933567748564354L;

    public ValidationError(String propertyName, String errorMessage, String attemptedValue) {
        this.propertyName = propertyName;
        this.errorMessage = errorMessage;
        this.attemptedValue = attemptedValue;
    }

    private String propertyName;
    private String errorMessage;
    private String attemptedValue;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getAttemptedValue() {
        return attemptedValue;
    }

    public void setAttemptedValue(String attemptedValue) {
        this.attemptedValue = attemptedValue;
    }
}