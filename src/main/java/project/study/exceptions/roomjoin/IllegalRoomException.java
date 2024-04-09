package project.study.exceptions.roomjoin;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

@Getter
public class IllegalRoomException extends RuntimeException {

    private final HttpServletResponse response;
    private final String alertMessage;

    public IllegalRoomException(HttpServletResponse response, String alertMessage) {
        this.response = response;
        this.alertMessage = alertMessage;
    }

}
