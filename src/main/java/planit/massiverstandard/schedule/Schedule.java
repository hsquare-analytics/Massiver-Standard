package planit.massiverstandard.schedule;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.BaseEntity;
import planit.massiverstandard.group.entity.Group;

import java.util.UUID;

@Getter
@Entity
@Table(name = "massiver_st_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

    @Id
    @Column(name = "schedule_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "cron", nullable = false)
    private String cron;

    @Builder
    public Schedule(Group group, String cron) {
        this.id = UUID.randomUUID();
        this.group = group;
        this.cron = cron;
    }
}
