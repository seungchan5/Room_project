package project.study.dto.login.responsedto;

import lombok.Getter;
import lombok.Setter;
import project.study.dto.abstractentity.ResponseDto;

import java.util.List;

@Getter
@Setter
public class ResponseSignupdto extends ResponseDto {

    private List<Error> errorList;

    public ResponseSignupdto(String result, String message, List<Error> errorList) {
        super(result, message);
        this.errorList = errorList;
    }
}
