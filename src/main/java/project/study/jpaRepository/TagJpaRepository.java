package project.study.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.study.domain.Room;
import project.study.domain.Tag;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    void deleteAllByRoom(Room room);
}
