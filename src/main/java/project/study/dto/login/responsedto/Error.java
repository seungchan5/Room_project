package project.study.dto.login.responsedto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Error {

    private String location;
    private String message;
}
