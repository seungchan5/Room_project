package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.study.common.CustomDateTime;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROOM_DELETE")
public class RoomDelete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomDeleteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    private LocalDateTime roomDeleteDate;

    public boolean isOutOfExpireDate() {
        return roomDeleteDate.isBefore(CustomDateTime.now());
    }
}
