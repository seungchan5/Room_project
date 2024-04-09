package project.study.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class ImageFileEntityChildren {

    private String originalName;
    private String storeName;

    public void setImage(String originalName, String storeName) {
        this.originalName = originalName;
        this.storeName = storeName;
    }
}
