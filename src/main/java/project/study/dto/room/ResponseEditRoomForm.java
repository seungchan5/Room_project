package project.study.dto.room;

import lombok.*;
import project.study.dto.abstractentity.ResponseDto;
import project.study.enums.PublicEnum;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseEditRoomForm {

    private String image;
    private String title;
    private String intro;
    private int max;
    private PublicEnum roomPublic;
    private String password;
    private List<String> tagList;

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}
