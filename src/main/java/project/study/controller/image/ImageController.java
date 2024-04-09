package project.study.controller.image;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
public class ImageController {

    @Value("${file.dir}")
    private String fileDir;
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름
    private final AmazonS3 amazonS3;

    @GetMapping(value = "/images/{fileUploadType}/{filename}", produces = {"image/png", "image/jpg", "image/jpeg"})
    public Resource downloadImage1(@PathVariable(name = "fileUploadType") String fileUploadType, @PathVariable(name = "filename") String filename) throws MalformedURLException {
        FileUploadType type = FileUploadType.findDir(fileUploadType);
//        return new UrlResource("file:" + getFullPath(filename, type));
        return new UrlResource(amazonS3.getUrl(bucketName, getFullPathS3(filename, type)));
    }
    @GetMapping("/images/{filename}")
    public Resource downloadImage2(@PathVariable(name = "filename") String filename) throws MalformedURLException {
//        return new UrlResource("file:" + getFullPath(filename, null));
        return new UrlResource(amazonS3.getUrl(bucketName, getFullPathS3(filename, null)));
    }

    private String getFullPath(String fileName, FileUploadType type) {
        StringBuilder sb = new StringBuilder(fileDir);

        if (type == null) return sb.append("/").append(fileName).toString();

        String folderName = type.getDir();
        return sb.append(folderName).append("/").append(fileName).toString();
    }

    private String getFullPathS3(String fileName, FileUploadType fileType) {
        StringBuilder sb = new StringBuilder("images/");

        if (fileType == null) return sb.append(fileName).toString();

        String folderName = fileType.getDir();
        return sb.append(folderName).append("/").append(fileName).toString();
    }
}
