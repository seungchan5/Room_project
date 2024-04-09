package project.study.authority.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import project.study.enums.PublicEnum;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SearchRoomDto {

    private Long roomId;
    private String roomTitle;
    private String roomMemberCount;
    private String managerName;
    private String roomCreateDate;
    private PublicEnum publicEnum;

}
