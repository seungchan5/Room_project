package project.study.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleManager {

    private final ScheduleService scheduleService;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void deleteExpireMember() {

        log.info("탈퇴 예정 회원 삭제 로직 시작");
        scheduleService.deleteExpireMember();
        log.info("탈퇴 예정 회원 삭제 로직 종료");

        log.info("삭제 예정 채팅방 삭제 로직 시작");
        scheduleService.deleteRoom();
        log.info("삭제 예정 채팅방 삭제 로직 종료");
    }
}
