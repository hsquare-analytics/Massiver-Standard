package planit.massiverstandard.group.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import planit.massiverstandard.BaseEntity;
import planit.massiverstandard.Executable;
import planit.massiverstandard.exception.group.GroupNameRequireException;
import planit.massiverstandard.group.dto.request.GroupDto;
import planit.massiverstandard.schedule.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "massiver_st_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends BaseEntity implements Executable {

    @Id
    private UUID id;

    private String name; // 그룹명

    @Comment("활성화 여부")
    private Boolean isActive;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GroupUnit> groupUnits = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    @Builder
    public Group(String name, List<GroupUnit> groupUnits) {

        if (name == null) {
            throw new GroupNameRequireException("ETL Group 이름을 입력해야 합니다.");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.isActive = false;
        this.groupUnits = groupUnits;
    }

    public void active() {
        this.isActive = true;
    }

    public void deActive() {
        this.isActive = false;
    }

    public List<Schedule> updateSchedule(List<Schedule> newSchedules) {

        // 1. 기준: cron 문자열로 Set 생성
        Set<String> newCronSet = newSchedules.stream()
            .map(Schedule::getCron)
            .collect(Collectors.toSet());

        Set<String> currentCronSet = this.schedules.stream()
            .map(Schedule::getCron)
            .collect(Collectors.toSet());

        // 2. 삭제 대상: 기존에 있지만 새 목록엔 없는 cron
        List<Schedule> toRemove = this.schedules.stream()
            .filter(s -> !newCronSet.contains(s.getCron()))
            .toList();

        // 3. 추가 대상: 새 목록엔 있지만 기존에 없는 cron
        List<Schedule> toAdd = newSchedules.stream()
            .filter(s -> !currentCronSet.contains(s.getCron()))
            .toList();

        // 4. 실제 제거/추가 수행
        this.schedules.removeAll(toRemove);
        this.schedules.addAll(toAdd);

        return this.schedules;
    }

    public void addGroupUnits(List<GroupUnit> groupUnitList) {
        this.groupUnits.addAll(groupUnitList);
        groupUnitList.forEach(groupUnit -> groupUnit.assignGroup(this));
    }

    public Group updateGroup(GroupDto groupDto, List<GroupUnit> groupUnitList) {
        this.name = groupDto.name();
        this.groupUnits.clear();
        this.groupUnits.addAll(groupUnitList);
        return this;
    }
}
