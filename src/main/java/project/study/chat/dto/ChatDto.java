package project.study.chat.dto;

import lombok.*;
import project.study.chat.MessageType;
import project.study.domain.Member;

import java.time.LocalDateTime;

@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatDto {

    private MessageType type;
    private Long roomId;
    private String token;
    private String sender;
    private String senderImage;
    private String message;
    private LocalDateTime time;

    public MessageType getType() {
        return type;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getSender() {
        return sender;
    }

    public String getSenderImage() {
        return senderImage;
    }
}
