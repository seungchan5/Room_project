package project.study.controller.api.kakaologin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;
//    @GetMapping("/login/kakao")
//    public void kakaologin(@RequestParam(name = "code") String code, HttpServletRequest request, HttpServletResponse response) {
//        HttpSession session = request.getSession();
//        kakaoLoginService.login(code, session, response);
//    }

}
