package project.study.domain;

import jakarta.persistence.*;
import lombok.*;
import project.study.common.CustomDateTime;
import project.study.constant.WebConst;
import project.study.dto.abstractentity.ResponseDto;
import project.study.exceptions.RestFulException;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ROOM_NOTICE")
public class RoomNotice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_NOTICE_ID")
    private Long roomNoticeId;

    private String roomNoticeContent;
    private LocalDateTime roomNoticeDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM")
    private Room room;

    public void updateNotice(String notice) {
        this.roomNoticeContent = notice;
        this.roomNoticeDate = CustomDateTime.now();
    }

    public ResponseRoomNotice buildResponseRoomNotice() {
        return ResponseRoomNotice
            .builder()
            .content(roomNoticeContent)
            .time(roomNoticeDate)
            .build();
    }

    @Getter
    @Builder
    public static class ResponseRoomNotice {

        private String content; // 공지사항 내용
        private LocalDateTime time; // 공지사항 시간

    }

    @Getter
    @Setter
    public static class RequestNoticeDto {

        private String notice;

        public void validNotice() {
            if (this.notice.length() > 300) {
                throw new RestFulException(new ResponseDto(WebConst.ERROR, "300자 이내로 작성해주세요."));
            }
        }

        public RoomNotice saveRoomNotice(Room room) {
            return RoomNotice.builder()
                    .room(room)
                    .roomNoticeContent(this.notice)
                    .roomNoticeDate(CustomDateTime.now())
                    .build();
        }
    }


}
