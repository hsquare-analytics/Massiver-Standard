package planit.massiverstandard.group.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.UUID;

import static planit.massiverstandard.group.entity.GroupUnitType.GROUP;

@Entity
@Getter
@Table(name = "massiver_st_group_unit")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupUnit {

    @Id
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "child_unit_id")
    private Unit childUnit;

    @ManyToOne
    @JoinColumn(name = "child_group_id")
    private Group childGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private GroupUnitType groupUnitType;

    // DAG 기반 실행 순서를 위한 depends_on 관계 추가 (해당 UNIT이 실행되기 위해 선행되어야 하는 UNIT들)
    @BatchSize(size = 100)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "massiver_st_group_unit_dependency",
        joinColumns = @JoinColumn(name = "group_unit_id"),
        inverseJoinColumns = @JoinColumn(name = "depends_on_group_unit_id")
    )
    private List<GroupUnit> parentGroupUnits;


    public boolean isChildUnit() {
        return groupUnitType == GroupUnitType.UNIT;
    }

    public boolean isChildGroup() {
        return groupUnitType == GROUP;
    }

    public UUID getExecutableId() {
        return isChildGroup() ? childGroup.getId() : childUnit.getId();
    }

    @Builder
    public GroupUnit(Group childGroup, Unit childUnit, GroupUnitType groupUnitType , List<GroupUnit> parentGroupUnits) {
        this.id = UUID.randomUUID();
        this.group = null;
        this.childGroup = childGroup;
        this.childUnit = childUnit;
        this.groupUnitType = groupUnitType;
        this.parentGroupUnits = parentGroupUnits;
    }

    public void assignGroup(Group group) {
        this.group = group;
    }

    public void assignParentGroupUnits(List<GroupUnit> parentGroupUnits) {
        this.parentGroupUnits.addAll(parentGroupUnits);
    }
}
