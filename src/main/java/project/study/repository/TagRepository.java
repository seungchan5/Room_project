package project.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import project.study.domain.QRoom;
import project.study.domain.QTag;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TagRepository {

    private final JPAQueryFactory query;

    public List<String> findAllByRoomId(Long roomId) {
        QTag t = QTag.tag;
        return query.select(t.tagName)
            .from(t)
            .where(QRoom.room.roomId.eq(roomId))
            .fetch();
    }
}
