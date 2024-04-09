package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "NOTIFY_IMAGE")
public class NotifyImage extends ImageFileEntityChildren {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notifyImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTIFY_ID")
    private Notify notify;

    public NotifyImage(Notify notify, String originalName, String storeName) {
        super(originalName, storeName);
        this.notify = notify;
    }

}
