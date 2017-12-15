package stu.lanyu.springdocker.response;

import stu.lanyu.springdocker.utility.StringUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ValidationErrors extends AbstractResponse implements Serializable {

    private static final long serialVersionUID = -2693929450995853879L;

    public ValidationErrors()
    {
        this.errorItems = new ArrayList<ValidationError>();
    }

    private List<ValidationError> errorItems;

    public ValidationErrors AddError(String propertyName, String attemptedValue) {

        if (StringUtility.isNullOrEmpty(propertyName))
            throw new NullPointerException("propertyName");

        this.errorItems.add(new ValidationError(propertyName, attemptedValue));

        return this;
    }

    public ValidationErrors AddUnhandledException(Exception ex) {
        this.errorItems.clear();
        this.errorItems.add(new ValidationError("UnhandledException", ExceptionUtils.getStackTrace(ex)));
        return this;
    }

    public List<ValidationError> getErrorItems() {
        return errorItems;
    }

    public void setErrorItems(List<ValidationError> errorItems) {
        this.errorItems = errorItems;
    }
}
