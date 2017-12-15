package stu.lanyu.springdocker.response;

import java.io.Serializable;

public class ApiResponse extends AbstractResponse implements Serializable {

    private static final long serialVersionUID = -7070961525499379098L;

    public ApiResponse() {
        errors = new ValidationErrors();
    }

    public long entityId;
    public ValidationErrors errors;
    public String additional;
    public boolean judageResult;
    public boolean isValid;
    public String errorMessage;

    public static ApiResponse createDomainSuccess() {
        ApiResponse response = new ApiResponse();
        response.isValid = true;
        return response;
    }

    public static ApiResponse createDomainSuccess(long entityId) {
        ApiResponse response = createDomainSuccess();
        response.entityId = entityId;
        return response;
    }

    public static ApiResponse createDomainSuccess(boolean judageResult) {
        ApiResponse response = createDomainSuccess();
        response.judageResult = judageResult;
        return response;
    }

    public  static ApiResponse createDomainFailure(String errorMessage, ValidationErrors errors) {
        ApiResponse response = createDomainFailure(errors);
        response.errorMessage = errorMessage;
        return  response;
    }

    public static ApiResponse createDomainFailure(ValidationErrors errors) {
        ApiResponse response = new ApiResponse();
        response.isValid = false;
        if (errors != null && errors.getErrorItems().size() > 0) {
            response.errors = new ValidationErrors();
            for (ValidationError error : errors.getErrorItems()) {
                response.errors.AddError(error.getPropertyName(), error.getErrorMessage(), error.getAttemptedValue());
            }
        }
        return response;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public ValidationErrors getErrors() {
        return errors;
    }

    public void setErrors(ValidationErrors errors) {
        this.errors = errors;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public boolean isJudageResult() {
        return judageResult;
    }

    public void setJudageResult(boolean judageResult) {
        this.judageResult = judageResult;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
