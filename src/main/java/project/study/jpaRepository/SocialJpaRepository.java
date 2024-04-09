package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Member;
import project.study.domain.Social;
import project.study.enums.SocialEnum;

import java.util.Optional;

public interface SocialJpaRepository extends JpaRepository<Social, Long> {

    Optional<Social> findBySocialTypeAndSocialEmail(SocialEnum socialType, String socialEmail);

    boolean existsBySocialEmail(String email);

    void deleteByMember(Member member);
}
