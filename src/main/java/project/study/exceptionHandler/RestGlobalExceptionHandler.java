package project.study.exceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.study.constant.WebConst;
import project.study.exceptions.authority.AdminAuthorizationException;
import project.study.exceptions.authority.AuthorizationException;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;
import project.study.exceptions.authority.NotAuthorizedException;
import project.study.exceptions.authority.NotLoginMemberException;
import project.study.exceptions.kakaologin.SocialException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestControllerAdvice
public class RestGlobalExceptionHandler {


    @ExceptionHandler(RestFulException.class)
    public ResponseEntity<ResponseDto> globalRestFulException(RestFulException e) {
        e.printStackTrace();
        log.error("[Global RestFul Exception 발생!] : {}", e.getResponseDto().getMessage());

        return new ResponseEntity<>(e.getResponseDto(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SocialException.class)
    public ResponseEntity<String> globalSocialException(SocialException e) {
        e.printStackTrace();
        log.error("[Global SocialException Exception 발생!]");
        execute(e.getResponse(), e.getAlertMessage());
        return null;
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ResponseDto> globalAuthorizationException(AuthorizationException e) {
        e.printStackTrace();
        log.error("[Global AuthorizationException Exception 발생!]");
        HttpServletResponse response = e.getResponse();
        String alert = e.getAlertMessage();
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");

        String command = "<script> " + getOption(alert) + " window.location.href='/'; </script>";
        try (PrintWriter out = response.getWriter()) {
            out.println(command);
            out.flush();
        } catch (IOException ex) {
            log.error("Alert IOException 발생!");
        }
        return new ResponseEntity<>(e.getResponseDto(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AdminAuthorizationException.class)
    public ResponseEntity<ResponseDto> globalAdminAuthorizationException(AdminAuthorizationException e) {
        e.printStackTrace();
        log.error("[Global AuthorizationException Exception 발생!]");
        HttpServletResponse response = e.getResponse();
        String alert = e.getAlertMessage();
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");

        String command = "<script> " + getOption(alert) + " window.location.href='/admin/login'; </script>";
        try (PrintWriter out = response.getWriter()) {
            out.println(command);
            out.flush();
        } catch (IOException ex) {
            log.error("Alert IOException 발생!");
        }
        return new ResponseEntity<>(e.getResponseDto(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ResponseDto> globalAuthorizationException(NotAuthorizedException e) {
        e.printStackTrace();
        log.error("[Global NotAuthorizedException Exception 발생!]");
        return new ResponseEntity<>(new ResponseDto(WebConst.ERROR, e.getAlertMessage()), HttpStatus.BAD_REQUEST);
    }

    private String execute(HttpServletResponse response, String alert) {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");

        String command = "<script> " + getOption(alert) + " window.self.close(); </script>";
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
