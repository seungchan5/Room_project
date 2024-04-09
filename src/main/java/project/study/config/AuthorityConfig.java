package project.study.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import project.study.authority.admin.authority.*;
import project.study.authority.member.authority.ManagerAuthority;
import project.study.authority.member.authority.ManagerAuthorityImpl;
import project.study.authority.member.authority.MemberAuthority;
import project.study.authority.member.authority.MemberAuthorityImpl;
import project.study.repository.MemberRepository;
import project.study.service.AdminService;
import project.study.service.JoinRoomService;
import project.study.service.NotifyService;
import project.study.service.RoomService;

@Configuration
@RequiredArgsConstructor
public class AuthorityConfig {

    private final RoomService roomService;
    private final JoinRoomService joinRoomService;
    private final AdminService adminService;
    private final MemberRepository memberRepository;
    private final NotifyService notifyService;

    @Bean
    @Primary
    public BanAuthority banAuthority() {
        return new BanAuthorityImpl(adminService);
    }

    @Bean
    @Primary
    public ExpireMemberInfoAuthority expireMemberInfoAuthority() {
        return new ExpireMemberInfoAuthorityImpl(adminService);
    }
    @Bean
    @Primary
    public MemberInfoAuthority memberInfoAuthority() {
        return new MemberInfoAuthorityImpl(adminService);
    }
    @Bean
    @Primary
    public NotifyMemberInfoAuthority notifyMemberInfoAuthority() {
        return new NotifyMemberInfoAuthorityImpl(adminService);
    }
    @Bean
    @Primary
    public RoomInfoAuthority roomInfoAuthority() {
        return new RoomInfoAuthorityImpl(adminService);
    }
    @Bean
    @Primary
    public ManagerAuthority managerAuthority() {
        return new ManagerAuthorityImpl(roomService, memberRepository);
    }
    @Bean
    @Primary
    public MemberAuthority memberAuthority() {
        return new MemberAuthorityImpl(roomService, joinRoomService, notifyService);
    }
}
