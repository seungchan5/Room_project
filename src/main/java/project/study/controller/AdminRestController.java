package project.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.study.authority.admin.AdminAuthorizationCheck;
import project.study.authority.admin.OverallAdmin;
import project.study.authority.admin.ReportAdmin;
import project.study.authority.admin.dto.*;
import project.study.domain.Admin;
import project.study.service.AdminService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRestController {

    private final AdminService adminService;
    private final AdminAuthorizationCheck check;

    @PostMapping("/login.do")
    public ResponseEntity<String> adminLogin(@RequestParam(value = "account") String account,
                           @RequestParam(value = "password") String password,
                           HttpSession session){

        Optional<Admin> findAdmin = adminService.adminLogin(account, password);
        if (findAdmin.isEmpty()) return ResponseEntity.ok("/admin/login");

        Admin admin = findAdmin.get();
        session.setAttribute("adminId", admin.getAdminId());

        if (admin.isOverall()) return ResponseEntity.ok("/admin/members");
        if (admin.isReport()) return ResponseEntity.ok("/admin/notify");
        else return ResponseEntity.ok("/admin/login");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> adminLogout(HttpServletRequest request){
        HttpSession session = request.getSession();

        if(session!=null){
            session.invalidate();
        }
        return ResponseEntity.ok("/admin/login");
    }

    @GetMapping("/members/get")
    public ResponseEntity<Page<SearchMemberDto>> searchNotify(@RequestParam(value = "word", required = false) String word,
                               @RequestParam(value = "onlyFreeze", required = false) String freezeOnly,
                               @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                               @SessionAttribute(name = "adminId", required = false) Long adminId,
                               HttpServletResponse response){

        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);

        Page<SearchMemberDto> page = overallAdmin.searchMemberList(pageNumber, word, freezeOnly);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/expire/get")
    public ResponseEntity<Page<SearchExpireMemberDto>> searchExpireMember(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                                                                          @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                                                                          @SessionAttribute(name = "adminId", required = false) Long adminId,
                                                                          HttpServletResponse response){

        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);

        Page<SearchExpireMemberDto> page = overallAdmin.searchExpireMemberList(pageNumber, word);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/rooms/get")
    public ResponseEntity<Page<SearchRoomDto>> searchRoom(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                                                          @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                                                          @SessionAttribute(name = "adminId", required = false) Long adminId,
                                                          HttpServletResponse response){

        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);

        Page<SearchRoomDto> page = overallAdmin.searchRoomList(pageNumber, word);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/notify/get")
    public ResponseEntity<Page<SearchNotifyDto>> searchNotify(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                                                              @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                                                              @RequestParam(value = "containComplete", required = false) String containComplete,
                                                              @SessionAttribute(name = "adminId", required = false) Long adminId,
                                                              HttpServletResponse response){

        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);

        Page<SearchNotifyDto> page = reportAdmin.searchNotifyList(pageNumber, word, containComplete);

        return ResponseEntity.ok(page);
    }

//    @PostMapping("/notify/status/change")
//    public void notifyStatusChange(@RequestBody RequestNotifyStatusChangeDto dto,
//                                   @SessionAttribute(name = "adminId", required = false) Long adminId,
//                                   HttpServletResponse response){
//        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);
//        reportAdmin.notifyComplete(dto);
//    }
//
//    @PostMapping("/notify/member/freeze")
//    public void notifyMemberFreeze (@RequestBody RequestNotifyMemberFreezeDto dto,
//                                    @SessionAttribute(name = "adminId", required = false) Long adminId,
//                                    HttpServletResponse response){
//        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);
//        reportAdmin.notifyFreeze(dto, response);
//
//    }
//
//    @PostMapping("/room/delete")
//    public void deleteRoom(@RequestBody RequestDeleteRoomDto dto,
//                           @SessionAttribute(name = "adminId", required = false) Long adminId,
//                           HttpServletResponse response){
//        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);
//        overallAdmin.deleteRoom(dto);
//    }

    @GetMapping("/bans/get")
    public ResponseEntity<Page<SearchBanDto>> searchBan(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                                                          @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                                                          @SessionAttribute(name = "adminId", required = false) Long adminId,
                                                          HttpServletResponse response){

        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);

        Page<SearchBanDto> page = reportAdmin.searchBanList(pageNumber, word);

        return ResponseEntity.ok(page);
    }

//    @PostMapping("/ban/lift")
//    public void liftTheBan(@RequestBody RequestLiftBanDto dto,
//                           @SessionAttribute(name = "adminId", required = false) Long adminId,
//                           HttpServletResponse response){
//
//        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);
//        reportAdmin.liftTheBan(dto);
//    }
}
