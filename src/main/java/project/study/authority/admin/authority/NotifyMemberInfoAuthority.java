package project.study.authority.admin.authority;

import org.springframework.data.domain.Page;
import project.study.authority.admin.dto.*;

public interface NotifyMemberInfoAuthority {

    Page<SearchNotifyDto> searchNotifyList(int pageNumber, String word, String containComplete);
    SearchNotifyReadMoreDtoBatis notifyReedMoreBatis(Long notifyId);
    SearchNotifyMemberInfoDto searchNotifyMemberInfo(Long notifyId, String account);

}
