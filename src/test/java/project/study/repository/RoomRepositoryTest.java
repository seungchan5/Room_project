package project.study.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.domain.MockRoom;
import project.study.domain.Room;
import project.study.exceptions.authority.joinroom.FullRoomException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("RoomRepository")
class RoomRepositoryTest {

    @Autowired
    private MockRoom mockRoom;

    @Test
    @DisplayName("방이 가득차지 않았을 때 방에 참가")
    void validFullRoom1() {
        // given
        Room room = mockRoom.createRoom().setMaxPerson(3).addJoinRoom(2).build();


        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestJoinRoomDto data = new RequestJoinRoomDto(null, room, response, null);


        // 예외가 발생하지 않음
        assertThatCode(data::validFullRoom)
            .doesNotThrowAnyException();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    @DisplayName("방이 가득찼을 때 방에 참가")
    void validFullRoom2() {
        // given
        Room room = mockRoom.createRoom().setMaxPerson(3).addJoinRoom(3).build();

        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestJoinRoomDto data = new RequestJoinRoomDto(null, room, response, null);

        // 예외가 발생함
        assertThatThrownBy(() -> {
            data.validFullRoom();
            assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()); // BAD_REQUEST 검증
        })
            .isInstanceOf(FullRoomException.class) // 예외가 발생하는지 검증
            .hasFieldOrPropertyWithValue("alertMessage", "방이 가득찼습니다."); // 메세지 검증

    }

}