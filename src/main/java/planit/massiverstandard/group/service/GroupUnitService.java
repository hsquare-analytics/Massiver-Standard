package planit.massiverstandard.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.Executable;
import planit.massiverstandard.config.Mapper;
import planit.massiverstandard.group.dto.request.GroupUnitDto;
import planit.massiverstandard.group.dto.request.ParentGroupUnitDto;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.entity.GroupUnit;
import planit.massiverstandard.group.entity.GroupUnitType;
import planit.massiverstandard.group.repository.GroupUnitRepository;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.FindUnit;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static planit.massiverstandard.group.entity.GroupUnitType.GROUP;

@Service
@RequiredArgsConstructor
public class GroupUnitService {

    private final GroupUnitRepository groupUnitRepository;

    private final FindGroup findGroup;
    private final FindUnit findUnit;

    private final Mapper mapper;


    public boolean istExistByUnit(UUID unitId) {
        return groupUnitRepository.existsByChildUnit_Id(unitId);
    }

    @Transactional("transactionManager")
    public List<GroupUnit> createGroupUnitList(List<GroupUnitDto> groupUnitDtos) {
        // 1️⃣ Unit 정보를 캐싱하고 그래프 초기화
        Map<UUID, GroupUnitDto> dtoMap = groupUnitDtos.stream()
            .collect(Collectors.toMap(GroupUnitDto::id, Function.identity()));
        Map<UUID, Unit> unitMap = new HashMap<>(); // 캐싱된 Unit 정보
        Map<UUID, Group> groupMap = new HashMap<>(); // 캐싱된 Group 정보
        Map<Executable, List<Executable>> graph = new HashMap<>();
        Map<Executable, Integer> inDegree = new HashMap<>(); // 진입 차수

        // 2️⃣ 위상 정렬을 위한 그래프 구성
        for (GroupUnitDto groupUnitDto : groupUnitDtos) {

            Executable executable = storeCache(groupUnitDto.groupUnitType(), groupUnitDto.id(), groupMap, unitMap);

            graph.putIfAbsent(executable, new ArrayList<>());
            inDegree.putIfAbsent(executable, 0);

            for (ParentGroupUnitDto parentUnitDto : groupUnitDto.parentIds()) {

                Executable parentExecutable = storeCache(parentUnitDto.groupUnitType(), parentUnitDto.id(), groupMap, unitMap);
                graph.putIfAbsent(parentExecutable, new ArrayList<>());

                graph.get(parentExecutable).add(executable); // 부모 → 자식 관계 추가
                inDegree.put(executable, inDegree.getOrDefault(executable, 0) + 1); // 진입 차수 증가
            }
        }

        // 3️⃣ 진입 차수가 0인 노드를 큐에 삽입 (부모 먼저 실행)
        Queue<Executable> readyQueue = new LinkedList<>();
        for (Executable excutable : inDegree.keySet()) {
            if (inDegree.get(excutable) == 0) {
                readyQueue.add(excutable);
            }
        }

        Map<UUID,GroupUnit> savedGroupUnitMap = new HashMap<>();

        // 4️⃣ 위상 정렬을 통한 부모부터 실행
        while (!readyQueue.isEmpty()) {
            List<Executable> parallelExecutables = new ArrayList<>();

            // 병렬 실행 가능한 Unit들 가져오기
            while (!readyQueue.isEmpty()) {
                parallelExecutables.add(readyQueue.poll());
            }

            // 5️⃣ 병렬 실행 → `GroupUnit` 생성 후 저장
            for (Executable executable : parallelExecutables) {

                GroupUnitDto dto = dtoMap.get(executable.getId());

                GroupUnit groupUnit;
                if (dto.groupUnitType().equals(GROUP)) {
                    Group group = groupMap.get(dto.id());
                    groupUnit = mapper.toGroupEntity(group, List.of(), dto);
                } else {
                    Unit unit = unitMap.get(dto.id());
                    groupUnit = mapper.toUnitEntity(unit, List.of(), dto);
                }

                groupUnit = groupUnitRepository.save(groupUnit); // ✅ 먼저 persist
                savedGroupUnitMap.put(dto.id(), groupUnit);
            }

            // 5️⃣ 병렬 실행 → `GroupUnit` 생성 후 저장
            for (Executable executable : parallelExecutables) {
                GroupUnitDto dto = dtoMap.get(executable.getId());

                List<UUID> parentIdList = dto.parentIds().stream().map(ParentGroupUnitDto::id).toList();
                List<GroupUnit> parentGroupUnits = parentIdList.stream()
                    .map(savedGroupUnitMap::get)
                    .filter(Objects::nonNull) // 부모가 저장되지 않은 경우 방지
                    .toList();

                savedGroupUnitMap.get(executable.getId()).assignParentGroupUnits(parentGroupUnits);
            }

            // 6️⃣ 실행 완료된 Unit 기반으로 다음 실행할 Unit 추가
            for (Executable executable : parallelExecutables) {
                for (Executable dependent : graph.getOrDefault(executable, new ArrayList<>())) {
                    inDegree.put(dependent, inDegree.get(dependent) - 1);
                    if (inDegree.get(dependent) == 0) {
                        readyQueue.add(dependent);
                    }
                }
            }
        }

        return savedGroupUnitMap.values().stream()
            .map(groupUnitRepository::save)
            .toList();
    }

    private Executable storeCache(GroupUnitType type, UUID id, Map<UUID, Group> groupMap, Map<UUID, Unit> unitMap) {
        Executable executable;
        if (type.equals(GROUP)) {
            executable = groupMap.computeIfAbsent(id, findGroup::byId);
        } else {
            executable = unitMap.computeIfAbsent(id, findUnit::byId);
        }
        return executable;
    }

}
