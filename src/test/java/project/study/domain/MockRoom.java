package project.study.domain;


import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.study.common.CustomDateTime;
import project.study.controller.image.FileUpload;
import project.study.controller.image.FileUploadType;
import project.study.enums.AuthorityMemberEnum;
import project.study.enums.PublicEnum;
import project.study.jpaRepository.RoomJpaRepository;

import java.time.LocalDateTime;
import java.util.List;


@Component
@Transactional()
public class MockRoom {

    @Autowired
    private RoomJpaRepository roomJpaRepository;
    @Autowired
    private MockMember mockMember;
    @Autowired
    private EntityManager em;
    @Autowired
    private FileUpload fileUpload;


    public MockRoomBuilder createRoom() {
        Room saveRoom = Room.builder()
            .roomTitle("테스트용 방 제목")
            .roomIntro("테스트용 방 소개글")
            .roomPublic(PublicEnum.PUBLIC)
            .roomLimit(4)
            .roomCreateDate(CustomDateTime.now())
            .build();
        em.persist(saveRoom);

        fileUpload.saveFile(null, FileUploadType.ROOM_PROFILE, saveRoom);

        return new MockRoomBuilder(roomJpaRepository, mockMember, em, saveRoom);
    }
    public MockRoomBuilder createRoom(PublicEnum publicEnum, RoomPassword roomPassword) {
        Room saveRoom = Room.builder()
            .roomTitle("테스트용 방 제목")
            .roomIntro("테스트용 방 소개글")
            .roomPublic(publicEnum)
            .roomPassword(roomPassword)
            .roomLimit(4)
            .roomCreateDate(CustomDateTime.now())
            .build();
        em.persist(saveRoom);
        return new MockRoomBuilder(roomJpaRepository, mockMember, em, saveRoom);
    }


    public static class MockRoomBuilder{

        private final RoomJpaRepository roomJpaRepository;
        private final MockMember mockMember;
        private final EntityManager em;
        private Room room;

        public MockRoomBuilder(RoomJpaRepository roomJpaRepository, MockMember mockMember, EntityManager em, Room room) {
            this.roomJpaRepository = roomJpaRepository;
            this.mockMember = mockMember;
            this.em = em;
            this.room = room;
        }

        public MockRoomBuilder addTags() {
            Tag tag1 = new Tag("1", room);
            Tag tag2 = new Tag("2", room);
            Tag tag3 = new Tag("3", room);
            Tag tag4 = new Tag("4", room);
            Tag tag5 = new Tag("5", room);
            List<Tag> tagList = List.of(tag1, tag2, tag3, tag4, tag5);
            for (Tag tag : tagList) {
                em.persist(tag);
            }
            return this;
        }

        public MockRoomBuilder addJoinRoom(int count) {
            if (count == 0) return this;

            for (int i = 0; i < count; i++) {
                Member member = mockMember.createMember().setBasic().build();
                em.persist(createJoinRoom(i, member));
            }
            return this;
        }

        public MockRoomBuilder setMaxPerson(int value) {
            room.setRoomLimit(value);
            return this;
        }

        private JoinRoom createJoinRoom(int i, Member member) {
            return JoinRoom.builder()
                .room(room)
                .member(member)
                .authorityEnum((i == 0) ? AuthorityMemberEnum.방장 : AuthorityMemberEnum.일반)
                .build();
        }

        public Room build() {
            em.flush();
            em.clear();
            return roomJpaRepository.findById(room.getRoomId()).get();
        }
    }


}
