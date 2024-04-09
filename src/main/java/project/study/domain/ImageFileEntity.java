package project.study.domain;

public interface ImageFileEntity {

    void setImage(String originalName, String storeName);
    String getStoreImage();
    default boolean isDefaultImage() {
        return false;
    }

}
