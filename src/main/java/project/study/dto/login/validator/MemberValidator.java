package project.study.dto.login.validator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.study.domain.Freeze;
import project.study.domain.Member;
import project.study.dto.login.requestdto.RequestSignupDto;
import project.study.repository.FreezeRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public abstract class MemberValidator {

    private final FreezeRepository freezeRepository;

    public final void validLogin(Member member, HttpServletResponse response) {
        System.out.println("회원 검증 로직 시작");

        if (member == null) return;
        // 이용정지 회원인지 확인
        if (member.isExpireMember()) {
            expireMemberLoginException(response); // 탈퇴한 회원인지 확인
        }
        if(!member.isFreezeMember()) return; // 이용정지된 회원이 아님

        Optional<Freeze> findFreeze = freezeRepository.findByMemberId(member.getMemberId());
        if (findFreeze.isEmpty()) return; // Freeze Entity 없음 ( 혹시 모를 예외 처리 )

        Freeze freeze = findFreeze.get();
        if (freeze.isFinish()) { // Freeze Entity는 존재하지만 이용정지 기간에 풀린 경우
            freezeRepository.delete(freeze, member);
            return;
        }
        freezeMemberLoginException(freeze, response); // 모든 조건에 걸리지 않은 회원은 이용정지 회원임.
    }

    abstract void expireMemberLoginException(HttpServletResponse response);
    abstract void freezeMemberLoginException(Freeze freeze, HttpServletResponse response);

    public abstract void validSignup(RequestSignupDto signupDto);
}
