package project.study.authority.admin;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import project.study.authority.admin.authority.BanAuthority;
import project.study.authority.admin.authority.NotifyMemberInfoAuthority;
import project.study.authority.admin.dto.*;

@Component
@RequiredArgsConstructor
public class ReportAdmin implements NotifyMemberInfoAuthority, BanAuthority {

    private final NotifyMemberInfoAuthority notifyMemberInfoAuthority;
    private final BanAuthority banAuthority;

    @Override
    public void notifyComplete(RequestNotifyStatusChangeDto dto) {
        banAuthority.notifyComplete(dto);
    }


    @Override
    public void notifyFreeze(RequestNotifyMemberFreezeDto dto, HttpServletResponse response) {
        banAuthority.notifyFreeze(dto, response);
    }

    @Override
    public Page<SearchNotifyDto> searchNotifyList(int pageNumber, String word, String containComplete) {
        return notifyMemberInfoAuthority.searchNotifyList(pageNumber, word, containComplete);
    }

    @Override
    public SearchNotifyReadMoreDtoBatis notifyReedMoreBatis(Long notifyId) {
        return notifyMemberInfoAuthority.notifyReedMoreBatis(notifyId);
    }


    @Override
    public SearchNotifyMemberInfoDto searchNotifyMemberInfo(Long notifyId, String account) {
        return notifyMemberInfoAuthority.searchNotifyMemberInfo(notifyId, account);
    }

    @Override
    public Page<SearchBanDto> searchBanList(int pageNumber, String word) {
        return banAuthority.searchBanList(pageNumber, word);
    }

    @Override
    public void liftTheBan(RequestLiftBanDto dto) {
        banAuthority.liftTheBan(dto);
    }
}
