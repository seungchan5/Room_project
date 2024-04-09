package project.study.controller.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.study.domain.ImageFileEntity;
import software.amazon.ion.IonException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUpload {

    @Value("${file.dir}")
    private String fileDir;
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름
    private final AmazonS3 amazonS3;

    private final FileUploadRepository fileUploadRepository;

    public void saveFile(@Nullable MultipartFile imageFile, final FileUploadType fileType, final ImageFileEntity parentEntity) {
        FileUploadDto fileUploadDto = FileUploadDto.builder().parent(parentEntity).type(fileType).build();

        if (imageFile == null) {
            fileUploadRepository.saveImage(fileUploadDto);
            return;
        }

        if (isNotImage(imageFile)) return;

        String originalFileName = imageFile.getOriginalFilename();
        String storeFileName = createFileName(originalFileName);

        try {
            uploadIntoAmazonS3(imageFile, fileType, storeFileName); // 아마존 S3 업로드
//            createImageFile(imageFile, fileType, storeFileName); // 로컬 업로드
        } catch (IOException e) {
            log.error("saveFile transferTo error = {}", imageFile.getName());
            return;
        }

        fileUploadDto.setImage(originalFileName, storeFileName);
        fileUploadRepository.saveImage(fileUploadDto);
    }

    private void uploadIntoAmazonS3(MultipartFile imageFile, FileUploadType fileType, String storeFileName) throws IOException {
        File file = convert(imageFile, fileType).orElseThrow(IOException::new);
        String fullPath = getFullPathS3(storeFileName, fileType);
        amazonS3.putObject(new PutObjectRequest(bucketName, fullPath, file));
        file.delete();
    }

    public void editFile(@Nullable MultipartFile imageFile, FileUploadType fileType, ImageFileEntity parentEntity) {
        FileUploadDto fileUploadDto = FileUploadDto.builder().parent(parentEntity).type(fileType).build();

        if (imageFile == null) {
            fileUploadRepository.editImage(fileUploadDto);
            return;
        }

        if (isNotImage(imageFile)) return;

        String originalFileName = imageFile.getOriginalFilename();
        String storeFileName = createFileName(originalFileName);

        try {
            uploadIntoAmazonS3(imageFile, fileType, storeFileName);
//            createImageFile(imageFile, fileType, storeFileName);
        } catch (IOException e) {
            log.error("editFile transferTo error = {}", imageFile.getName());
            return;
        }

        // 이전 이미지 파일 삭제
//        fileUploadRepository.deleteImage(fileType, parentEntity); // 로컬 파일 삭제
        deleteObjectIntoS3(fileType, parentEntity.getStoreImage()); // 아마존 S3 파일 삭제

        fileUploadDto.setImage(originalFileName, storeFileName);
        fileUploadRepository.editImage(fileUploadDto);
    }

    // 신고하기 첨부파일은 썸네일을 적용하지 않음
    private void createImageFile(MultipartFile imageFile, FileUploadType fileType, String storeFileName) throws IOException {
        if (FileUploadType.NOTIFY_IMAGE.equals(fileType)) {
            imageFile.transferTo(new File(getFullPathLocal(storeFileName, fileType)));
            return;
        }

        Thumbnails.of(imageFile.getInputStream())
                .size(100, 100)
                .toFile(new File(getFullPathLocal(storeFileName, fileType)));
    }

    private void deleteObjectIntoS3(FileUploadType fileType, String storeName) {
        String path = String.format("/%s/%s", fileType.getDir(), storeName);
        amazonS3.deleteObject(bucketName, path);
    }

    private boolean isNotImage(MultipartFile file) {
        return file.getContentType() == null || !file.getContentType().startsWith("image/");
    }

    private String getFullPathLocal(String fileName, FileUploadType fileType) {
        StringBuilder sb = new StringBuilder(fileDir);

        if (fileType == null) return sb.append(fileName).toString();

        String folderName = fileType.getDir();
        return sb.append(folderName).append("/").append(fileName).toString();
    }

    private String getFullPathS3(String fileName, FileUploadType fileType) {
        StringBuilder sb = new StringBuilder("images/");

        if (fileType == null) return sb.append(fileName).toString();

        String folderName = fileType.getDir();
        return sb.append(folderName).append("/").append(fileName).toString();
    }

    public Optional<File> convert(MultipartFile multipartFile, FileUploadType fileType) throws IOException{
        File convertFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        if (convertFile.createNewFile()){

            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(multipartFile.getBytes());

                if (!FileUploadType.NOTIFY_IMAGE.equals(fileType)) { // NotifyImage가 아니면 썸네일로 저장
                    Thumbnails.of(multipartFile.getInputStream())
                            .size(100, 100)
                            .toFile(convertFile);
                }

                return Optional.of(convertFile);
            }
        }
        return Optional.empty();
    }


    @NotNull
    private String createFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();

        int pos = originalFileName.lastIndexOf(".");
        String ext = originalFileName.substring(pos);

        return uuid + ext;
    }

}