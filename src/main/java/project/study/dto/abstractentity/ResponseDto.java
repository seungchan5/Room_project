package project.study.dto.abstractentity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.study.constant.WebConst;

@Getter
@AllArgsConstructor
public class ResponseDto {

    private final String result;
    private final String message;

    public ResponseDto(String message) {
        this.result = WebConst.OK;
        this.message = message;
    }
}
