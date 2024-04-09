package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Room;
import project.study.domain.RoomPassword;

public interface RoomPasswordJpaRepository extends JpaRepository<RoomPassword, Long> {
    void deleteByRoom(Room room);
}
