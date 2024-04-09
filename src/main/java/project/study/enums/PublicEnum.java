package project.study.enums;

public enum PublicEnum {

    PUBLIC,
    PRIVATE;

    public boolean isPublic() {
        return this == PUBLIC;
    }
}
