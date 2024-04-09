package project.study.authority.admin.authority;

import org.springframework.data.domain.Page;
import project.study.authority.admin.dto.SearchExpireMemberDto;

public interface ExpireMemberInfoAuthority {

    Page<SearchExpireMemberDto> searchExpireMemberList(int pageNumber, String word);

}
