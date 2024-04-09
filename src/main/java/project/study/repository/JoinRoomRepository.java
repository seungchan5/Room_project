package project.study.repository;

import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.study.domain.*;
import project.study.enums.AuthorityMemberEnum;
import project.study.jpaRepository.JoinRoomJpaRepository;

import java.util.List;
import java.util.Optional;

import static project.study.domain.QRoom.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JoinRoomRepository {

    private final JoinRoomJpaRepository joinRoomJpaRepository;
    private final JPAQueryFactory query;


    public void createJoinRoom(Room room, Member member) {
        JoinRoom saveJoinRoom = JoinRoom.builder()
            .room(room)
            .member(member)
            .authorityEnum(AuthorityMemberEnum.방장)
            .build();
        joinRoomJpaRepository.save(saveJoinRoom);
    }


    public List<Room> search(String word, Pageable pageable) {
        StringExpression keywordExpression = getKeywordExpression(word);

        return query.select(room)
                .from(room)
                .leftJoin(room.roomDelete, QRoomDelete.roomDelete)
                .where((getLowerAndReplace(room.roomTitle).like(keywordExpression)
                    .or(getLowerAndReplace(room.roomIntro).like(keywordExpression))
                    .or(getLowerAndReplace(room.tags.any().tagName).like(keywordExpression)))
                    .and(room.roomDelete.isNull()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }


    private StringExpression getKeywordExpression(String word) {
        return Expressions.asString("%" + word.toLowerCase().replace(" ", "") + "%");
    }

    private StringExpression getLowerAndReplace(StringExpression tuple) {
        return Expressions.stringTemplate("replace(lower({0}), ' ', '')", tuple);
    }


    public boolean exitsByMemberAndRoom(Long memberId, Room room) {
        return joinRoomJpaRepository.existsByMember_memberIdAndRoom(memberId, room);
    }

    public JoinRoom save(JoinRoom saveJoinRoom) {
        return joinRoomJpaRepository.save(saveJoinRoom);
    }

    public Optional<JoinRoom> findByMemberAndRoom(Member member, Room room) {
        return joinRoomJpaRepository.findByMemberAndRoom(member, room);
    }

    public void deleteJoinRoom(JoinRoom joinRoom) {
        joinRoomJpaRepository.delete(joinRoom);
    }
}
