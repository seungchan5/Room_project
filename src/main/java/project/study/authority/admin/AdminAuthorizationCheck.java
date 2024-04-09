package project.study.authority.admin;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.study.domain.Admin;
import project.study.exceptions.authority.AdminAuthorizationException;
import project.study.exceptions.authority.AuthorizationException;
import project.study.service.AdminService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminAuthorizationCheck {

    private final AdminService adminService;
    private final OverallAdmin overallAdmin;
    private final ReportAdmin reportAdmin;

    public OverallAdmin getOverallAdmin(Long adminId, HttpServletResponse response) {
        Optional<Admin> findAdmin = adminService.findById(adminId);
        if (findAdmin.isEmpty() || !findAdmin.get().isOverall()) {
            throw new AdminAuthorizationException(response, "권한이 없습니다");
        }
        return overallAdmin;
    }

    public ReportAdmin getReportAdmin(Long adminId, HttpServletResponse response){
        Optional<Admin> findAdmin = adminService.findById(adminId);

        if (findAdmin.isEmpty() || !findAdmin.get().isReport()) {
            throw new AdminAuthorizationException(response, "권한이 없습니다");
        }
        return reportAdmin;
    }
}
