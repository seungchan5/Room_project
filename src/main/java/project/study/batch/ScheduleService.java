package project.study.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.study.domain.Member;
import project.study.domain.RoomDelete;
import project.study.enums.MemberStatusEnum;
import project.study.jpaRepository.MemberJpaRepository;
import project.study.jpaRepository.RoomDeleteJpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final MemberJpaRepository memberJpaRepository;
    private final RoomDeleteJpaRepository roomDeleteJpaRepository;

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void deleteExpireMember() {
        List<Member> expireMemberList = memberJpaRepository.findAllByMemberStatus(MemberStatusEnum.탈퇴);
        if (expireMemberList.isEmpty()) {
            log.info("탈퇴예정 회원 없음");
            return;
        }

        for (Member member : expireMemberList) {
            if (!member.isOutOfExpireDate()) continue;

            boolean hasNotifyAsCriminal = scheduleRepository.hasNotifyAsCriminal(member);
            if (hasNotifyAsCriminal) {
                log.info("신고처리가 완료되지않은 회원입니다. 닉네임 : {}", member.getMemberNickname());
                continue;
            }
            String nickname = member.getMemberNickname();
            log.info("탈퇴진행 닉네임 : {}", nickname);
            scheduleRepository.deleteJoinRoom(member);
            scheduleRepository.deleteAccount(member);
            scheduleRepository.deleteFreeze(member);
            scheduleRepository.deleteNotify(member);
            scheduleRepository.deleteMember(member);
            log.info("탈퇴완료 닉네임 : {}", nickname);

        }
    }

    @Transactional
    public void deleteRoom() {
        List<RoomDelete> roomDeleteList = roomDeleteJpaRepository.findAll();

        for (RoomDelete roomDelete : roomDeleteList) {
            if (!roomDelete.isOutOfExpireDate()) continue;

            scheduleRepository.deleteRoom(roomDelete);
        }
    }
}
