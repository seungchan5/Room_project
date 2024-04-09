package project.study.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ROOM_PASSWORD")
public class RoomPassword {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_PASSWORD_ID")
    private Long roomPasswordId;

    private String roomPassword;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    public boolean compareRoomPassword(String comparePassword) {
        return roomPassword.equals(comparePassword);
    }

    public void changeRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }
}
