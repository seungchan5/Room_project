package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.NotifyImage;

public interface NotifyImageJpaRepository extends JpaRepository<NotifyImage, Long> {
}
