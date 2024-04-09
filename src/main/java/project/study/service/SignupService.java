package project.study.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.study.exceptions.signup.DistinctAccountException;
import project.study.exceptions.signup.DistinctNicknameException;
import project.study.jpaRepository.BasicJpaRepository;
import project.study.jpaRepository.MemberJpaRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private final BasicJpaRepository basicJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    public void distinctAccount(String account) {
        boolean existsByAccount = basicJpaRepository.existsByAccount(account);
        if (existsByAccount) throw new DistinctAccountException();
    }


    public void distinctNickname(String nickname) {
        boolean existsByNickname = memberJpaRepository.existsByMemberNickname(nickname);
        if (existsByNickname) throw new DistinctNicknameException();
    }
}
