package project.study.chat.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

@Getter
public class ChatObject<T> extends ChatDto {

    private final T data;

    public ChatObject(ChatDto chat, T data) {
        super(chat.getType(), chat.getRoomId(), chat.getToken(), chat.getSender(), chat.getSenderImage(), chat.getMessage(), chat.getTime());
        this.data = data;
    }

}
