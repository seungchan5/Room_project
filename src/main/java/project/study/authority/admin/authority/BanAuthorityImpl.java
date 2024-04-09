package project.study.authority.admin.authority;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import project.study.authority.admin.dto.RequestLiftBanDto;
import project.study.authority.admin.dto.RequestNotifyMemberFreezeDto;
import project.study.authority.admin.dto.RequestNotifyStatusChangeDto;
import project.study.authority.admin.dto.SearchBanDto;
import project.study.service.AdminService;

@Component
@RequiredArgsConstructor
public class BanAuthorityImpl implements BanAuthority{

    private final AdminService adminService;

    @Override
    public void notifyComplete(RequestNotifyStatusChangeDto dto) {
        adminService.notifyComplete(dto);
    }

    @Override
    public void notifyFreeze(RequestNotifyMemberFreezeDto dto, HttpServletResponse response) {
        adminService.notifyFreeze(dto, response);
    }

    @Override
    public Page<SearchBanDto> searchBanList(int pageNumber, String word) {
        return adminService.searchBanList(pageNumber, word);
    }

    @Override
    public void liftTheBan(RequestLiftBanDto dto) {
        adminService.liftTheBan(dto);
    }
}
