package project.study.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.RequestEditRoomDto;
import project.study.common.CustomDateTime;
import project.study.controller.image.FileUpload;
import project.study.controller.image.FileUploadType;
import project.study.domain.*;
import project.study.authority.member.dto.RequestCreateRoomDto;
import project.study.dto.room.ResponseEditRoomForm;
import project.study.dto.room.ResponseRoomMemberList;
import project.study.enums.AuthorityMemberEnum;
import project.study.enums.PublicEnum;
import project.study.exceptions.roomjoin.IllegalRoomException;
import project.study.jpaRepository.RoomDeleteJpaRepository;
import project.study.jpaRepository.RoomJpaRepository;
import project.study.jpaRepository.RoomPasswordJpaRepository;
import project.study.jpaRepository.TagJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RoomRepository {

    private final RoomJpaRepository roomJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final RoomPasswordJpaRepository roomPasswordJpaRepository;
    private final RoomDeleteJpaRepository roomDeleteJpaRepository;
    private final FileUpload fileUpload;
    private final JPAQueryFactory query;


    @Transactional
    public Room createRoom(RequestCreateRoomDto data) {
        Room saveRoom = Room.builder()
            .roomTitle(data.getTitle())
            .roomIntro(data.getIntro())
            .roomLimit(data.getMax())
            .roomPublic(data.getRoomPublic())
            .roomCreateDate(CustomDateTime.now())
            .build();
        return roomJpaRepository.save(saveRoom);
    }
    @Transactional
    public void createRoomImage(RequestCreateRoomDto data, Room room) {
        fileUpload.saveFile(data.getImage(), FileUploadType.ROOM_PROFILE, room);
    }
    @Transactional
    public void createTags(RequestCreateRoomDto data, Room room) {
        List<String> tags = data.getTags();
        for (String tag : tags) {
            Tag saveTag = new Tag(tag, room);
            tagJpaRepository.save(saveTag);
        }
    }

    @Transactional
    public void createPassword(RequestCreateRoomDto data, Room room) {
        if (data.getRoomPublic() == PublicEnum.PUBLIC) return;

        RoomPassword saveRoomPassword = RoomPassword.builder()
            .roomPassword(data.getRoomPassword())
            .room(room)
            .build();

        roomPasswordJpaRepository.save(saveRoomPassword);
    }


    public Long getNumberFormat(String roomIdStr, HttpServletResponse response) {
        try {
            return Long.parseLong(roomIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalRoomException(response, "존재하지 않는 방입니다.");
        }
    }

    public Optional<Room> findById(Long roomId) {
        return roomJpaRepository.findById(roomId);
    }

    public List<ResponseRoomMemberList> getResponseRoomMemberList(Room room, Member member) {
        QJoinRoom j = QJoinRoom.joinRoom;
        QMember m = QMember.member;
        QProfile p = QProfile.profile;

        return query.select(Projections.fields(ResponseRoomMemberList.class,
                    m.memberId.as("memberId"),
                    p.storeName.as("image"),
                    m.memberNickname.as("name"),
                    m.memberId.eq(member.getMemberId()).as("isMe"),
                    j.authorityEnum.stringValue().eq(AuthorityMemberEnum.방장.name()).as("isManager")
                ))
                .from(j)
                .join(m).on(j.member.memberId.eq(m.memberId))
                .leftJoin(p).on(p.member.memberId.eq(m.memberId))
                .where(j.room.roomId.eq(room.getRoomId()))
                .fetch();
    }

    private BooleanExpression isManager(EnumPath<AuthorityMemberEnum> authorityEnum) {
        return authorityEnum.eq(AuthorityMemberEnum.방장);
    }

    public ResponseEditRoomForm getResponseEditRoomForm(Room room) {
        QRoom r = QRoom.room;
        QRoomImage ri = QRoomImage.roomImage;
        QRoomPassword p = QRoomPassword.roomPassword1;

        return query
            .select(Projections.fields(ResponseEditRoomForm.class,
                ri.storeName.as("image"),
                r.roomTitle.as("title"),
                r.roomIntro.as("intro"),
                r.roomLimit.as("max"),
                r.roomPublic.as("roomPublic"),
                p.roomPassword.as("password")
            ))
            .from(r)
            .join(ri).on(r.roomImage.roomImageId.eq(ri.roomImageId))
            .leftJoin(p).on(r.roomId.eq(p.room.roomId))
            .where(r.roomId.eq(room.getRoomId()))
            .fetchFirst();
    }

    public void editRoomImage(Room room, RequestEditRoomDto data) {
        if (data.isDefaultImage()) data.setImage(null);
        fileUpload.editFile(data.getImage(), FileUploadType.ROOM_PROFILE, room);
    }

    public void editRoomPassword(Room room, RequestEditRoomDto data) {
        RoomPassword roomPassword = room.getRoomPassword();

        if (data.getRoomPublic().isPublic()) {                          // 수정내역이 PUBLIC 인 경우
            if (roomPassword != null) {                                     // 패스워드 삭제
                roomPasswordJpaRepository.delete(roomPassword);
            }
        } else {                                                        // 수정내역이 PRIVATE 인 경우
            if (roomPassword == null) {                                     // 패스워드가 설정되어있지 않을 경우
                RoomPassword saveRoomPassword = RoomPassword.builder()
                    .room(room)
                    .roomPassword(data.getRoomPassword())
                    .build();
                roomPasswordJpaRepository.save(saveRoomPassword);
            } else {                                                        // 이미 패스워드가 설정되어있는 경우
                roomPassword.changeRoomPassword(data.getRoomPassword());
            }
        }
    }

    public void editTag(Room room, RequestEditRoomDto data) {
        tagJpaRepository.deleteAllByRoom(room);

        List<String> tags = data.getTags();
        for (String tag : tags) {
            Tag saveTag = new Tag(tag, room);
            tagJpaRepository.save(saveTag);
        }
    }

    public void moveToDeleteRoom(Room room) {
        RoomDelete saveRoomDelete = RoomDelete.builder()
            .room(room)
            .roomDeleteDate(CustomDateTime.now().plusMonths(1))
            .build();

        roomDeleteJpaRepository.save(saveRoomDelete);
    }
}