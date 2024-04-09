package project.study.dto.login.validator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.study.domain.Freeze;
import project.study.domain.Member;
import project.study.dto.login.requestdto.RequestSignupDto;
import project.study.dto.login.requestdto.RequestSocialSignupDto;
import project.study.exceptions.kakaologin.AlreadySignupMemberException;
import project.study.exceptions.kakaologin.SocialException;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.jpaRepository.SocialJpaRepository;
import project.study.repository.FreezeRepository;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class SocialMemberValidator extends MemberValidator {

    private final SocialJpaRepository socialJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    public SocialMemberValidator(FreezeRepository freezeRepository, SocialJpaRepository socialJpaRepository, MemberJpaRepository memberJpaRepository) {
        super(freezeRepository);
        this.socialJpaRepository = socialJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    void expireMemberLoginException(HttpServletResponse response) {
        throw new SocialException(response, "탈퇴한 회원입니다."); // 탈퇴한 회원인지 확인
    }

    @Override
    void freezeMemberLoginException(Freeze freeze, HttpServletResponse response) {
        throw new SocialException(response, freeze.printMessage()); // 모든 조건에 걸리지 않은 회원은 이용정지 회원임.

    }

    @Override
    public void validSignup(RequestSignupDto signupDto) {
        RequestSocialSignupDto data = (RequestSocialSignupDto) signupDto;

        // 중복 가입인 경우
        boolean distinctEmail = socialJpaRepository.existsBySocialEmail(data.getEmail());
        if (distinctEmail) {
            throw new AlreadySignupMemberException(data.getResponse());
        }

        // 닉네임이 중복이라면 닉네임 + #숫자랜덤5자리 로 생성 중복이 아닐때까지 while 반복
        String nickname = data.getNickName();
        while (true) {
            boolean distinctNickname = memberJpaRepository.existsByMemberNickname(nickname);
            if (!distinctNickname) break;

            String random = "#" + new Random().ints(0, 9).limit(5).mapToObj(String::valueOf).collect(Collectors.joining());
            int index = nickname.indexOf("#");
            if (index == -1) {
                nickname += random;
            } else {
                nickname = nickname.substring(0, index) + random;
            }
        }
        data.setNickName(nickname);


        String phone = data.getPhone();
        // +82 10-1234-5678 -> 01012345678 문자열 변경
        if (phone.contains("+")) {
            phone = phone.replaceAll("(^\\+[0-9]+ )", "0").replace("-", "");
            data.setPhone(phone);
        }


    }
}
