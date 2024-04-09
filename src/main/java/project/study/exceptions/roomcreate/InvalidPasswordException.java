package project.study.exceptions.roomcreate;

import project.study.dto.abstractentity.ResponseDto;

public class InvalidPasswordException extends CreateRoomException{

    public InvalidPasswordException(ResponseDto responseDto) {
        super(responseDto);
    }
}
