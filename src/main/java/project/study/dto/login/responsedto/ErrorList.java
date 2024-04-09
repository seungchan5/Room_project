package project.study.dto.login.responsedto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorList {

    private final List<Error> errorList;

    public ErrorList() {
        this.errorList = new ArrayList<>();
    }

    public void addError(Error error) {
        errorList.add(error);
    }

    public boolean hasError() {
        return !errorList.isEmpty();
    }

}
