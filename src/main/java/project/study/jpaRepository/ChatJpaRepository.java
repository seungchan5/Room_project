package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Chat;
import project.study.domain.Room;

public interface ChatJpaRepository extends JpaRepository<Chat, Long> {

    void deleteAllByRoom(Room room);
}
