package com.elcollecte.mission.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mission_enqueteurs")
public class MissionEnqueteur {

    @EmbeddedId
    private MissionEnqueteurId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("missionId")
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Column(name = "enqueteur_id", insertable = false, updatable = false)
    private Long enqueteurId;

    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "is_active")
    private boolean active = true;

    @PrePersist
    protected void onCreate() { this.assignedAt = LocalDateTime.now(); }

    public MissionEnqueteur() {}

    public MissionEnqueteur(Mission mission, Long enqueteurId) {
        this.id = new MissionEnqueteurId(mission.getId(), enqueteurId);
        this.mission = mission;
        this.enqueteurId = enqueteurId;
        this.active = true;
    }

    public MissionEnqueteurId getId() { return id; }
    public Mission getMission() { return mission; }
    public Long getEnqueteurId() { return enqueteurId; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    @Embeddable
    public static class MissionEnqueteurId implements java.io.Serializable {
        @Column(name = "mission_id")
        private Long missionId;
        @Column(name = "enqueteur_id")
        private Long enqueteurId;

        public MissionEnqueteurId() {}
        public MissionEnqueteurId(Long missionId, Long enqueteurId) {
            this.missionId = missionId;
            this.enqueteurId = enqueteurId;
        }
        public Long getMissionId() { return missionId; }
        public Long getEnqueteurId() { return enqueteurId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MissionEnqueteurId that)) return false;
            return java.util.Objects.equals(missionId, that.missionId)
                && java.util.Objects.equals(enqueteurId, that.enqueteurId);
        }
        @Override
        public int hashCode() {
            return java.util.Objects.hash(missionId, enqueteurId);
        }
    }
}
