package project.study.dto.room;

import lombok.Getter;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.dto.abstractentity.ResponseDto;

import java.util.List;

@Getter
public class SearchRoomListDto extends ResponseDto {

    private final String word;
    private final List<ResponseRoomListDto> roomList;

    public SearchRoomListDto(String result, String message, String word, List<ResponseRoomListDto> roomList) {
        super(result, message);
        this.word = word;
        this.roomList = roomList;
    }

    public SearchRoomListDto(String message, String word, List<ResponseRoomListDto> roomList) {
        super(message);
        this.word = word;
        this.roomList = roomList;
    }
}
