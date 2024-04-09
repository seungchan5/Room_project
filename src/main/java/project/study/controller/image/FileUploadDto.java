package project.study.controller.image;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.study.domain.*;

import static project.study.constant.WebConst.*;

@Getter
@Builder
public class FileUploadDto {

    private ImageFileEntity parent;
    private String imageUploadName;
    private String imageStoreName;
    private FileUploadType type;

    public void defaultImageCheck() {
        if (imageUploadName == null || imageStoreName == null || imageUploadName.isEmpty() || imageStoreName.isEmpty()) {

            switch (type) {
                case MEMBER_PROFILE -> imageUploadName = imageStoreName = DEFAULT_PROFILE;
                case ROOM_PROFILE -> imageUploadName = imageStoreName = DEFAULT_ROOM_IMAGE;
            }
        }
    }

    public void setImage(String originalFileName, String storeFileName) {
        this.imageUploadName = originalFileName;
        this.imageStoreName = storeFileName;
    }
}
