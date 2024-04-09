package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.RoomDelete;

public interface RoomDeleteJpaRepository extends JpaRepository<RoomDelete, Long> {
}
