package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.authority.member.dto.RequestEditRoomDto;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.constant.WebConst;
import project.study.enums.PublicEnum;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room implements ImageFileEntity {

    @Getter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Getter
    private String roomTitle;

    @Getter
    private String roomIntro;

    @Getter
    private int roomLimit;

    private LocalDateTime roomCreateDate;

    @Enumerated(EnumType.STRING)
    private PublicEnum roomPublic;

    @Getter
    @OneToMany(mappedBy = "room")
    private List<Tag> tags;

    @Getter
    @OneToOne(mappedBy = "room", fetch = FetchType.LAZY)
    private RoomImage roomImage;

    @Getter
    @OneToOne(mappedBy = "room", fetch = FetchType.LAZY)
    private RoomPassword roomPassword;

    @Getter
    @OneToOne(mappedBy = "room", fetch = FetchType.LAZY)
    private RoomNotice roomNotice;

    @OneToOne(mappedBy = "room", fetch = FetchType.LAZY)
    private RoomDelete roomDelete;

    @Getter
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<JoinRoom> joinRoomList;

    @Getter
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Chat> chatHistory;

    public boolean isPublic() {
        return roomPublic.isPublic();
    }

    // 테스트용도로만 사용되는 메소드
    public void setRoomLimit(int roomLimit) {
        this.roomLimit = roomLimit;
    }

    public boolean hasNotice() {
        return roomNotice != null;
    }

    public boolean isDeleteRoom() {
        return roomDelete != null;
    }

    public int joinRoomSize() {
        return joinRoomList.size();
    }


    @Override
    public void setImage(String originalName, String storeName) {
        if (roomImage == null) return;
        roomImage.setImage(originalName, storeName);
    }

    @Override
    public String getStoreImage() {
        if (roomImage == null) return "";
        return roomImage.getStoreName();
    }

    @Override
    public boolean isDefaultImage() {
        return WebConst.DEFAULT_ROOM_IMAGE.equals(getStoreImage());
    }

    public ResponseRoomListDto getResponseRoomListDto(Long memberId) {
        return ResponseRoomListDto.builder()
                .roomId(roomId)
                .roomImage(getStoreImage())
                .roomTitle(roomTitle)
                .roomIntro(roomIntro)
                .roomPublic(isPublic())
                .roomJoin(memberId != null && joinRoomList.stream().anyMatch(joinRoom -> joinRoom.getMember().getMemberId().equals(memberId)))
                .nowPerson(joinRoomSize())
                .maxPerson(roomLimit)
                .tagList(tags.stream().map(Tag::getTagName).toList())
                .build();
    }



    public void editRoom(RequestEditRoomDto data) {
        roomTitle = data.getTitle();
        roomIntro = data.getIntro();
        roomPublic = data.getRoomPublic();
        roomLimit = data.getMax();
    }

    public RoomNotice.ResponseRoomNotice getChatInsideNotice() {
        if (!hasNotice()) return null;
        return roomNotice.buildResponseRoomNotice();
    }

    public void updateNotice(String notice) {
        if (!hasNotice()) return;
        roomNotice.updateNotice(notice);
    }

    public boolean hasRoomPassword() {
        return roomPassword != null;
    }
    public boolean isValidPassword(String password) {
        if (!hasRoomPassword()) return false;
        return roomPassword.compareRoomPassword(password);
    }

    public boolean isFullRoom() {
        int maxPerson = roomLimit;
        int nowPerson = joinRoomSize();
        return nowPerson >= maxPerson;
    }

    @Getter
    @Builder
    public static class ResponseRoomInfo {

        private String title;
        private int max;
        private boolean isPublic;
        private boolean isManager;

    }
    public ResponseRoomInfo getResponseRoomInfo(JoinRoom joinRoom) {
        return ResponseRoomInfo.builder()
                        .title(roomTitle)
                        .isPublic(isPublic())
                        .max(roomLimit)
                        .isManager(joinRoom.isManager())
                        .build();
    }

    @Getter
    @Builder
    public static class ResponseRoomUpdateInfo {

        private boolean isPublic;
        private String title;
        private int max;

    }

    public ResponseRoomUpdateInfo getResponseRoomUpdateInfo() {
        return ResponseRoomUpdateInfo.builder()
                .title(roomTitle)
                .isPublic(isPublic())
                .max(roomLimit)
                .build();
    }

    @Getter
    @Builder
    public static class ResponsePrivateRoomInfoDto {

        private final String image;
        private final String title;
        private final String intro;
    }
    public ResponsePrivateRoomInfoDto getResponsePrivateRoomInfo() {
        return ResponsePrivateRoomInfoDto.builder()
                .image(getStoreImage())
                .title(roomTitle)
                .intro(roomIntro)
                .build();
    }

}
