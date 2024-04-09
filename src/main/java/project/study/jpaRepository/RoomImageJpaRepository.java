package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Room;
import project.study.domain.RoomImage;

public interface RoomImageJpaRepository extends JpaRepository<RoomImage, Long> {
    void deleteByRoom(Room room);
}
