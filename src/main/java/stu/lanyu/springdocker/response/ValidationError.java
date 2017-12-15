package stu.lanyu.springdocker.response;

import java.io.Serializable;

public class ValidationError extends AbstractResponse implements Serializable {

    private static final long serialVersionUID = -455933567748564354L;

    public ValidationError(String propertyName, String attemptedValue) {
        this.propertyName = propertyName;
        this.attemptedValue = attemptedValue;
    }

    private String propertyName;
    private String attemptedValue;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getAttemptedValue() {
        return attemptedValue;
    }

    public void setAttemptedValue(String attemptedValue) {
        this.attemptedValue = attemptedValue;
    }
}