package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Room;
import project.study.domain.RoomNotice;

public interface RoomNoticeJpaRepository extends JpaRepository<RoomNotice, Long> {
    void deleteAllByRoom(Room room);
}
