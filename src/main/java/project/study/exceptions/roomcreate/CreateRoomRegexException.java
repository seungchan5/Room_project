package project.study.exceptions.roomcreate;

import project.study.dto.abstractentity.ResponseDto;

public class CreateRoomRegexException extends CreateRoomException{

    public CreateRoomRegexException(ResponseDto responseDto) {
        super(responseDto);
    }
}
