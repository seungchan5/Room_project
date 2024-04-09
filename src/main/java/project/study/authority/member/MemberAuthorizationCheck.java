package project.study.authority.member;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.study.authority.member.authority.ManagerAuthority;
import project.study.authority.member.authority.MemberAuthority;
import project.study.domain.JoinRoom;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.exceptions.authority.NotAuthorizedException;
import project.study.exceptions.authority.NotFoundRoomException;
import project.study.exceptions.authority.NotJoinRoomException;
import project.study.exceptions.authority.NotLoginMemberException;
import project.study.jpaRepository.JoinRoomJpaRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberAuthorizationCheck {

    private final ManagerAuthority managerAuthority;
    private final MemberAuthority memberAuthority;
    private final JoinRoomJpaRepository joinRoomJpaRepository;

    private void authorizationCheck(HttpServletResponse response,  Member member, Room room) {
        if (room == null) throw new NotFoundRoomException(response);

        Optional<JoinRoom> findJoinRoom = joinRoomJpaRepository.findByMemberAndRoom(member, room);
        if (findJoinRoom.isEmpty()) throw new NotJoinRoomException(response);

        JoinRoom joinRoom = findJoinRoom.get();
        if (!joinRoom.isManager()) throw new NotAuthorizedException(response);
    }

    public ManagerAuthority getManagerAuthority(HttpServletResponse response, Member member, Room room) {
        authorizationCheck(response, member, room);
        return managerAuthority;
    }

    public MemberAuthority getMemberAuthority(HttpServletResponse response, Member member) {
        if (member == null) throw new NotLoginMemberException(response);
        return memberAuthority;
    }
}
