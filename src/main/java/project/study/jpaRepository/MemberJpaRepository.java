package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Member;
import project.study.enums.MemberStatusEnum;

import java.util.List;
import java.util.Optional;


public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberNickname(String nickName);
    Optional<Member> findByMemberNameAndPhone(String name, String phone);
    Optional<Member> findByMemberNickname(String nickname);
    boolean existsByPhone(String phone);
    List<Member> findAllByMemberStatus(MemberStatusEnum status);
}
