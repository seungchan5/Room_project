package project.study.chat.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.study.chat.MessageType;
import project.study.chat.dto.ChatDto;
import project.study.common.CustomDateTime;
import project.study.domain.Member;
import project.study.domain.Room;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChatManager {

    private final ChatAccessToken chatAccessToken;

    public ChatDto sendMessageChatDto(Member member, Room room, MessageType messageType, String message) {
        return ChatDto.builder()
                .roomId(room.getRoomId())
                .token(chatAccessToken.getToken(member.getMemberId()))
                .type(messageType)
                .sender(member.getMemberNickname())
                .time(CustomDateTime.now())
                .message(message)
                .build();
    }
}