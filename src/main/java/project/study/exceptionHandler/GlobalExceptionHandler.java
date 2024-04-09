package project.study.exceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import project.study.exceptions.roomjoin.IllegalRoomException;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalRoomException.class)
    public String illegalRoomException(IllegalRoomException e) {
        log.info("[IllegalRoomException Handler 발생]");
        return execute(e.getResponse(), e.getAlertMessage());
    }

    private String execute(HttpServletResponse response, String alert) {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");

        String command = "<script> " + getOption(alert) + " window.location.href='/'; </script>";
        try (PrintWriter out = response.getWriter()) {
            out.println(command);
            out.flush();
        } catch (IOException e) {
            log.error("Alert IOException 발생!");
        }
        return null;
    }

    private String getOption(String option) {
        // redirect url이 있으면 url로 이동
        if (option == null) {
            return "opener.location.href='/';";
        }
        return String.format("alert('%s');", option);
    }
}
