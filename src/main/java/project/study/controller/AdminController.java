package project.study.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.study.authority.admin.AdminAuthorizationCheck;
import project.study.authority.admin.OverallAdmin;
import project.study.authority.admin.ReportAdmin;
import project.study.authority.admin.dto.*;
import project.study.domain.Admin;
import project.study.dto.admin.Criteria;
import project.study.repository.AdminMapper;
import project.study.service.AdminService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminAuthorizationCheck check;

    @GetMapping("/login")
    public String adminLogin(){
        return "admin/admin_login";
    }

    @GetMapping("/members")
    public String searchMember(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                               @RequestParam(value = "onlyFreeze", required = false) String freezeOnly,
                               @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                               @SessionAttribute(name = "adminId", required = false) Long adminId,
                               HttpServletResponse response, Model model){

        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);
        Admin admin = adminService.findById(adminId).get();

        Page<SearchMemberDto> searchMemberDtoList = overallAdmin.searchMemberList(pageNumber, word, freezeOnly);

        model.addAttribute("page", searchMemberDtoList);
        model.addAttribute("word", word);
        model.addAttribute("freezeOnly", freezeOnly != null);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());

        return "admin/admin_members";
    }

    @GetMapping("/expire")
    public String searchExpireMember(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                                     @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                                     @SessionAttribute(name = "adminId", required = false) Long adminId,
                                     HttpServletResponse response, Model model){

        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);
        Admin admin = adminService.findById(adminId).get();

        Page<SearchExpireMemberDto> searchExpireMemberList = overallAdmin.searchExpireMemberList(pageNumber, word);

        model.addAttribute("page", searchExpireMemberList);
        model.addAttribute("word", word);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());
        return "admin/admin_expire";
    }


    @GetMapping("/rooms")
    public String searchRoom(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                             @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                             @SessionAttribute(name = "adminId", required = false) Long adminId,
                             HttpServletResponse response, Model model){

        OverallAdmin overallAdmin = check.getOverallAdmin(adminId, response);
        Admin admin = adminService.findById(adminId).get();

        Page<SearchRoomDto> searchRoomList = overallAdmin.searchRoomList(pageNumber, word);

        model.addAttribute("page", searchRoomList);
        model.addAttribute("word", word);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());
        return "admin/admin_rooms";
    }

    @GetMapping("/notify")
    public String searchNotify(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                               @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                               @RequestParam(value = "containComplete", required = false) String containComplete,
                               @SessionAttribute(name = "adminId", required = false) Long adminId,
                               HttpServletResponse response, Model model){

        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);
        Admin admin = adminService.findById(adminId).get();

        Page<SearchNotifyDto> searchNotifyList = reportAdmin.searchNotifyList(pageNumber, word, containComplete);

        model.addAttribute("page", searchNotifyList);
        model.addAttribute("word", word);
        model.addAttribute("containComplete", containComplete != null);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());

        return "admin/admin_notify";
    }


    @GetMapping("/notify/read_more")
    public String notifyReadMore(@RequestParam(value = "notifyId") Long notifyId, Model model,
                                 @SessionAttribute(name = "adminId", required = false) Long adminId,
                                 HttpServletResponse response){
        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);

        SearchNotifyReadMoreDtoBatis searchNotifyReadMoreDtoBatis = reportAdmin.notifyReedMoreBatis(notifyId);
        model.addAttribute("notifyInfo", searchNotifyReadMoreDtoBatis);
        return "admin/notify_read_more";
    }

    @GetMapping("/notify/member_info")
    public String notifyMemberInfo(@RequestParam(value = "account") String account,
                                   @RequestParam(value = "notifyId") Long notifyId, Model model,
                                   @SessionAttribute(name = "adminId", required = false) Long adminId,
                                   HttpServletResponse response) {
        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);

        SearchNotifyMemberInfoDto searchNotifyMemberInfoDto = reportAdmin.searchNotifyMemberInfo(notifyId, account);
        model.addAttribute("memberInfo", searchNotifyMemberInfoDto);
        return "admin/notify_member";
    }

    @GetMapping("/bans")
    public String searchBan(@RequestParam(value = "word", required = false, defaultValue = "") String word,
                            @RequestParam(defaultValue = "1", value = "page") int pageNumber,
                            @SessionAttribute(name = "adminId", required = false) Long adminId,
                            HttpServletResponse response, Model model){

        ReportAdmin reportAdmin = check.getReportAdmin(adminId, response);
        Admin admin = adminService.findById(adminId).get();

        Page<SearchBanDto> searchBanDto = reportAdmin.searchBanList(pageNumber, word);
        model.addAttribute("page", searchBanDto);
        model.addAttribute("word", word);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("adminEnum", admin.getAdminEnum());

        return "admin/admin_ban";
    }
}
