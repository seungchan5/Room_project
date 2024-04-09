package project.study.dto.abstractentity;

import lombok.Getter;

@Getter
public class ResponseObject<T> extends ResponseDto {

    private final T data;

    public ResponseObject(String result, String message, T data) {
        super(result, message);
        this.data = data;
    }

    public ResponseObject(String message, T data) {
        super(message);
        this.data = data;
    }
}
