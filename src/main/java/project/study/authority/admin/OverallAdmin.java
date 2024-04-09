package project.study.authority.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import project.study.authority.admin.authority.*;
import project.study.authority.admin.dto.*;

@Component
@RequiredArgsConstructor
public class OverallAdmin implements ExpireMemberInfoAuthority, MemberInfoAuthority, RoomInfoAuthority  {

    private final ExpireMemberInfoAuthority expireMemberInfoAuthority;
    private final MemberInfoAuthority memberInfoAuthority;
    private final RoomInfoAuthority roomInfoAuthority;

    @Override
    public Page<SearchMemberDto> searchMemberList(int pageNumber, String word, String freezeOnly) {
        return memberInfoAuthority.searchMemberList(pageNumber, word, freezeOnly );
    }

    @Override
    public Page<SearchExpireMemberDto> searchExpireMemberList(int pageNumber, String word) {
        return expireMemberInfoAuthority.searchExpireMemberList(pageNumber, word);
    }

    @Override
    public Page<SearchRoomDto> searchRoomList(int pageNumber, String word) {
        return roomInfoAuthority.searchRoomList(pageNumber, word);
    }

    @Override
    public void deleteRoom(RequestDeleteRoomDto dto) {
        roomInfoAuthority.deleteRoom(dto);
    }

}
