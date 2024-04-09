package project.study.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;
import project.study.constant.WebConst;

import java.time.LocalDateTime;
import java.util.Comparator;

@Builder
@Entity
@Table(name = "CHAT")
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Comparable<Chat> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "MEMBER_ID")
    private Member sendMember;

    @Lob
    private String message;
    private LocalDateTime sendDate;

    @Override
    public int compareTo(@NotNull Chat o) {
        return (int) (this.chatId - o.chatId);
    }


    @Getter
    public static class ResponseChatHistory {

        private final boolean isMe;
        private final Long pageValue;
        private final String sender;
        private final String senderImage;
        private final String message;
        private final LocalDateTime time;

        public ResponseChatHistory(Long chatId, Member sendMember, String message, LocalDateTime sendDate, Long memberId) {
            if (sendMember == null) {
                this.isMe = false;
                this.sender = "탈퇴한 사용자";
                this.senderImage = WebConst.EXPIRE_MEMBER_PROFILE;
            } else {
                this.isMe = sendMember.getMemberId().equals(memberId);
                this.sender = sendMember.getMemberNickname();
                this.senderImage = sendMember.getStoreImage();
            }
            this.pageValue = chatId;
            this.message = message;
            this.time = sendDate;
        }

    }
    public ResponseChatHistory getResponseChatHistory(Long memberId) {
        return new ResponseChatHistory(chatId, sendMember, message, sendDate, memberId);
    }
}
