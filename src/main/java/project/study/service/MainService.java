package project.study.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.study.controller.api.kakaologin.KakaoLoginService;
import project.study.domain.Member;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainService {

    private final KakaoLoginService kakaoLoginService;

    public String logout(Member member) {
        if (!member.isSocialMember()) return "/";

        if (member.isKakao()) return kakaoLoginService.logout(member);

        return "/";
    }

}
