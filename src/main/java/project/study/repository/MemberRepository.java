package project.study.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import project.study.constant.WebConst;
import project.study.domain.Member;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;
import project.study.jpaRepository.MemberJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    public Member findByMemberNickname(String nickname) {
        Optional<Member> findMember = memberJpaRepository.findByMemberNickname(nickname);
        return findMember.orElseThrow(() -> new RestFulException(new ResponseDto(WebConst.ERROR, "존재하지 않는 회원입니다.")));
    }
}
