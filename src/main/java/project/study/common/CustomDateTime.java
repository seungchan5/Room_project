package project.study.common;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CustomDateTime {

    private static final ZoneId zoneId = ZoneId.of("Asia/Seoul");

    public static LocalDateTime now() {
        return LocalDateTime.now(zoneId);
    }
}
