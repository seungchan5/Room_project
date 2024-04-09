package project.study.exceptions;

import org.springframework.validation.BindingResult;
import project.study.constant.WebConst;
import project.study.dto.abstractentity.ResponseObject;
import project.study.dto.login.responsedto.Error;

public class BindingResultException extends RestFulException {

    public BindingResultException(String alertMessage, BindingResult bindingResult) {
        super(new ResponseObject<>(WebConst.ERROR, alertMessage,
            bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> new Error(fieldError.getField(), fieldError.getDefaultMessage()))
                .filter(error -> error.getMessage() != null)
                .toList()
        ));
    }
}
