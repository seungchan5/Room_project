package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import project.study.enums.NotifyStatus;
import project.study.enums.NotifyType;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "NOTIFY")
public class Notify implements ImageFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notifyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "REPORTER_MEMBER_ID")  // reporter 필드에 대한 컬럼명 변경
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CRIMINAL_MEMBER_ID")  // criminal 필드에 대한 컬럼명 변경
    private Member criminal;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @Enumerated(EnumType.STRING)
    private NotifyType notifyReason;
    private String notifyContent;
    private LocalDateTime notifyDate;

    @Enumerated(EnumType.STRING)
    private NotifyStatus notifyStatus;

    // Not Columns
    @OneToMany(mappedBy = "notify")
    private List<NotifyImage> notifyImage;

    @Override
    public void setImage(String originalName, String storeName) {}

    @Override
    public String getStoreImage() {
        return null;
    }

}
