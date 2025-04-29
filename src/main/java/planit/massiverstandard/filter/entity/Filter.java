package planit.massiverstandard.filter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import planit.massiverstandard.BaseEntity;
import planit.massiverstandard.unit.entity.Unit;

import java.util.Map;
import java.util.UUID;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "filter_type") // 구분 컬럼
@Table(name = "massiver_st_filter")
public abstract class Filter extends BaseEntity {

    @Id
    private UUID id;

    private String name;

    @Column(name = "filter_order")
    private int order;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    // ★ discriminator 값을 바로 읽어올 필드
    @Column(name = "filter_type", insertable = false, updatable = false)
    private String type;

    public Filter(Unit unit, String name, int order) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.order = order;
        this.unit = unit;
    }

    public abstract Map<String, Object> process(Map<String, Object> item);

    public void assignUnit(Unit unit) {
        this.unit = unit;
    }
}
