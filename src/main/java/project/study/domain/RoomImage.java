package project.study.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "ROOM_IMAGE")
public class RoomImage extends ImageFileEntityChildren {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_IMAGE_ID")
    private Long roomImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    public RoomImage(Room room, String originalName, String storeName) {
        super(originalName, storeName);
        this.room = room;
    }
}
