package project.study.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.study.dto.abstractentity.ResponseDto;
import project.study.dto.login.requestdto.RequestDefaultLoginDto;
import project.study.dto.login.requestdto.RequestDefaultSignupDto;
import project.study.service.LoginService;
import project.study.service.SignupService;

import static project.study.constant.WebConst.LOGIN_MEMBER;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;
    private final SignupService signupService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> defaultLogin(@RequestBody RequestDefaultLoginDto data, HttpSession session, HttpServletResponse response) {
        loginService.login(data, session, response);
        return ResponseEntity.ok(new ResponseDto("로그인 성공"));

    }

//    @PostMapping("/signup")
//    public ResponseEntity<ResponseDto> defaultSignup(@RequestBody RequestDefaultSignupDto data) {
//        loginService.signup(data);
//        return ResponseEntity.ok(new ResponseDto("회원가입 성공"));
//    }

    @PostMapping("/distinct/account")
    public ResponseEntity<ResponseDto> distinctAccount(@RequestBody(required = false) String account) {
        if (account == null || account.isBlank()) return ResponseEntity.ok(new ResponseDto("none", ""));

        signupService.distinctAccount(account);
        return ResponseEntity.ok(new ResponseDto("사용할 수 있는 아이디입니다."));
    }
    @PostMapping("/distinct/nickname")
    public ResponseEntity<ResponseDto> distinctNickname(@RequestBody(required = false) String nickname) {
        if (nickname == null || nickname.isBlank()) return ResponseEntity.ok(new ResponseDto("none", ""));

        signupService.distinctNickname(nickname);
        return ResponseEntity.ok(new ResponseDto("사용할 수 있는 닉네임입니다."));
    }

    @GetMapping("/login/check")
    public ResponseEntity<ResponseDto> hasLogin(@SessionAttribute(value = LOGIN_MEMBER, required = false) Long memberId) {
        return ResponseEntity.ok(new ResponseDto( (memberId != null) ? "Login User" : "Require Login"));
    }
}
