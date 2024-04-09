package project.study.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.study.authority.member.MemberAuthorizationCheck;
import project.study.authority.member.authority.MemberAuthority;
import project.study.authority.member.dto.RequestNotifyDto;
import project.study.customAnnotation.CallType;
import project.study.customAnnotation.PathRoom;
import project.study.customAnnotation.SessionLogin;
import project.study.domain.Member;
import project.study.domain.Room;
import project.study.dto.abstractentity.ResponseDto;

import static project.study.constant.WebConst.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotifyController {

    private final MemberAuthorizationCheck authorizationCheck;

    @PostMapping("/room/{room}/notify")
    public ResponseEntity<ResponseDto> notify(@SessionLogin(required = true, type = CallType.CONTROLLER) Member member,
                                              HttpServletResponse response,
                                              @PathRoom("room") Room room,
                                              @ModelAttribute RequestNotifyDto data) {
        MemberAuthority memberAuthority = authorizationCheck.getMemberAuthority(response, member);
        memberAuthority.notify(member, room, data);
        return ResponseEntity.ok(new ResponseDto(OK, "성공"));
    }
}
