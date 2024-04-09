package project.study.exceptions.signup;

import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

public class SignupException extends RestFulException {

    public SignupException(ResponseDto responseDto) {
        super(responseDto);
    }
}
