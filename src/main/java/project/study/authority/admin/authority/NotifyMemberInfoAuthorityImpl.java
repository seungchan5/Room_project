package project.study.authority.admin.authority;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import project.study.authority.admin.dto.*;
import project.study.service.AdminService;

@Component
@RequiredArgsConstructor
public class NotifyMemberInfoAuthorityImpl implements NotifyMemberInfoAuthority{

    private final AdminService adminService;

    @Override
    public Page<SearchNotifyDto> searchNotifyList(int pageNumber, String word, String containComplete) {
        return adminService.searchNotifyList(pageNumber, word, containComplete);
    }

    @Override
    public SearchNotifyReadMoreDtoBatis notifyReedMoreBatis(Long notifyId) {
        return adminService.notifyReedMoreBatis(notifyId);
    }

    @Override
    public SearchNotifyMemberInfoDto searchNotifyMemberInfo(Long notifyId, String account) {
        return adminService.searchNotifyMemberInfo(notifyId, account);
    }


}
