package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Ban;

public interface BanJpaRepository extends JpaRepository<Ban, Long> {
    boolean existsByBanPhone(String phone);
}
