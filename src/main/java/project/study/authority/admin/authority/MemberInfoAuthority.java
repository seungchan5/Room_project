package project.study.authority.admin.authority;

import org.springframework.data.domain.Page;
import project.study.authority.admin.dto.SearchMemberDto;

public interface MemberInfoAuthority {

    Page<SearchMemberDto> searchMemberList(int pageNumber, String word, String freezeOnly);

}
