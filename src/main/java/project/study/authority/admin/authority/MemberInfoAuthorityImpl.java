package project.study.authority.admin.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import project.study.authority.admin.dto.SearchMemberDto;
import project.study.service.AdminService;

@Component
@RequiredArgsConstructor
public class MemberInfoAuthorityImpl implements MemberInfoAuthority {

    private final AdminService adminService;

    @Override
    public Page<SearchMemberDto> searchMemberList(int pageNumber, String word, String freezeOnly) {
        return adminService.searchMemberList(pageNumber, word, freezeOnly);
    }


}
