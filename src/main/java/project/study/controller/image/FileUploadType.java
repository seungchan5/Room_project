package project.study.controller.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.study.domain.*;

@Getter
@AllArgsConstructor
public enum FileUploadType {

    MEMBER_PROFILE("member_profile", Profile.class),
    ROOM_PROFILE("room_profile", RoomImage.class),
    NOTIFY_IMAGE("notify_image", NotifyImage.class);

    private String dir;
    private Class<? extends ImageFileEntityChildren> aClass;

    public static FileUploadType findDir(String dir) {
        FileUploadType[] values = FileUploadType.values();
        for (FileUploadType value : values) {
            if (value.dir.equals(dir)) {
                return value;
            }
        }
        return null;
    }

    public ImageFileEntityChildren createEntity(FileUploadDto data) {

        if (this.aClass.isAssignableFrom(Profile.class)) {
            return new Profile((Member) data.getParent(), data.getImageUploadName(), data.getImageStoreName());

        }
        if (this.aClass.isAssignableFrom(RoomImage.class)) {
            return new RoomImage((Room) data.getParent(), data.getImageUploadName(), data.getImageStoreName());

        }
        if (this.aClass.isAssignableFrom(NotifyImage.class)) {
            return new NotifyImage((Notify) data.getParent(), data.getImageUploadName(), data.getImageStoreName());
        }
        return null;
    }
}
