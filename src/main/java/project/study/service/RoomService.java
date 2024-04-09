package project.study.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.member.dto.RequestEditRoomDto;
import project.study.authority.member.dto.ResponseRoomListDto;
import project.study.constant.WebConst;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.authority.member.dto.RequestCreateRoomDto;
import project.study.domain.RoomNotice;
import project.study.dto.abstractentity.ResponseDto;
import project.study.dto.room.*;
import project.study.enums.AuthorityMemberEnum;
import project.study.exceptions.roomcreate.CreateExceedRoomException;
import project.study.exceptions.roomjoin.IllegalRoomException;
import project.study.jpaRepository.JoinRoomJpaRepository;
import project.study.jpaRepository.RoomNoticeJpaRepository;
import project.study.repository.JoinRoomRepository;
import project.study.repository.RoomRepository;
import project.study.repository.TagRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final JoinRoomRepository joinRoomRepository;
    private final JoinRoomJpaRepository joinRoomJpaRepository;
    private final RoomNoticeJpaRepository roomNoticeJpaRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Long createRoom(RequestCreateRoomDto data, Member member) {
        Room room = roomRepository.createRoom(data);
        roomRepository.createRoomImage(data, room);
        roomRepository.createTags(data, room);
        roomRepository.createPassword(data, room);
        joinRoomRepository.createJoinRoom(room, member);
        return room.getRoomId();
    }


    public List<ResponseRoomListDto> getMyRoomList(Member member) {
        List<JoinRoom> joinRoomList = member.getJoinRoomList();

        List<ResponseRoomListDto> temp = new ArrayList<>(joinRoomList.size());
        for (JoinRoom joinRoom : joinRoomList) {
            temp.add(joinRoom.getResponseRoomListDto());
        }

        return temp;
    }


    public List<ResponseRoomListDto> searchRoomList(Long memberId, String word, Pageable pageable) {
        List<Room> roomInfo = joinRoomRepository.search(word, pageable);

        List<ResponseRoomListDto> temp = new ArrayList<>(roomInfo.size());
        for (Room room : roomInfo) {
            temp.add(room.getResponseRoomListDto(memberId));
        }
        return temp;
    }

    public void validMaxCreateRoom(Member member) {
        if (member.isExceedJoinRoom(AuthorityMemberEnum.방장)) {
            throw new CreateExceedRoomException(new ResponseDto(WebConst.ERROR, "방 생성 최대개수를 초과하였습니다."));
        }
    }

    public Room findByRoom(String roomIdStr, HttpServletResponse response) {
        Long roomId = roomRepository.getNumberFormat(roomIdStr, response);
        Optional<Room> findRoom = roomRepository.findById(roomId);
        return findRoom.orElseThrow(() -> new IllegalRoomException(response, "방 정보를 찾을 수 없습니다."));
    }
    public Optional<Room> findById(Long roomId) {
        if (roomId == null) return Optional.empty();
        return roomRepository.findById(roomId);
    }

    public List<ResponseRoomMemberList> getResponseRoomMemberList(Room room, Member member) {
        return roomRepository.getResponseRoomMemberList(room, member);
    }


    public ResponseEditRoomForm getEditRoomForm(Room room) {
        ResponseEditRoomForm form = roomRepository.getResponseEditRoomForm(room);
        form.setTagList(tagRepository.findAllByRoomId(room.getRoomId()));
        return form;
    }

    public void editRoom(Room room, RequestEditRoomDto data) {
        room.editRoom(data);

        roomRepository.editRoomImage(room, data);
        roomRepository.editRoomPassword(room, data);
        roomRepository.editTag(room, data);
    }

    public RoomNotice saveRoomNotice(Room room, RoomNotice.RequestNoticeDto data) {
        RoomNotice saveRoomNotice = data.saveRoomNotice(room);
        return roomNoticeJpaRepository.save(saveRoomNotice);
    }

    public void deleteRoomNotice(Room room) {
        RoomNotice roomNotice = room.getRoomNotice();
        roomNoticeJpaRepository.delete(roomNotice);
    }

    public void deleteJoinRoom(JoinRoom joinRoom) {
        joinRoomJpaRepository.delete(joinRoom);
    }
}