package project.study.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.RequestJoinRoomDto;
import project.study.component.CustomMvcResult;
import project.study.domain.MockRoom;
import project.study.domain.Room;

import static project.study.component.CustomMvcResult.HttpMethodType.POST;

@WebMvcTest(RoomController.class)
@Transactional
@DisplayName("RoomController")
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockRoom mockRoom;
    private final CustomMvcResult customMvcResult = new CustomMvcResult(mockMvc);


//    @Test
//    void validFullRoom() throws Exception {
//        // given
//        Room room = mockRoom.createRoom().setMaxPerson(3).addJoinRoom(2).addTags().build();
//        MockHttpServletResponse response = customMvcResult.getMvcResult(POST, "/room/1/edit", HttpStatus.OK).getResponse();
//
//        RequestJoinRoomDto requestJoinRoomDto = new RequestJoinRoomDto(null, room, response, null);
//        // when
//
//        // then
//    }
}
