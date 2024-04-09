package project.study.exceptions;

import lombok.Getter;
import project.study.dto.abstractentity.ResponseDto;

@Getter
public class RestFulException extends RuntimeException {

    private final ResponseDto responseDto;

    public RestFulException(ResponseDto responseDto) {
        this.responseDto = responseDto;
    }


}
