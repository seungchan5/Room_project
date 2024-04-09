package project.study.controller.image;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.study.domain.*;

import java.io.File;

@Repository
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileUploadRepository {

    @Value("${file.dir}")
    private String fileDir;
    private final EntityManager em;

    public void saveImage(FileUploadDto data) {
        data.defaultImageCheck();
        FileUploadType type = data.getType();
        ImageFileEntityChildren entity = type.createEntity(data);
        em.persist(entity);
    }


    public void editImage(FileUploadDto data) {
        data.defaultImageCheck();

        ImageFileEntity parent = data.getParent();

        parent.setImage(data.getImageUploadName(), data.getImageStoreName());
    }

    public void deleteImage(FileUploadType type, ImageFileEntity parent) {
        if (parent.isDefaultImage()) return;

        File profileFile = new File(getFullPath(type, parent.getStoreImage()));
        if (profileFile.delete()) {
            log.error("{} 파일 삭제", getFullPath(type, parent.getStoreImage()));
        }

    }

    private String getFullPath(FileUploadType type, String storeName) {
        return String.format("%s%s/%s", fileDir, type.getDir(), storeName);
    }
}
