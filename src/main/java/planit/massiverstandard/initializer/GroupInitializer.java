package planit.massiverstandard.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.Executable;
import planit.massiverstandard.group.dto.request.GroupDto;
import planit.massiverstandard.group.dto.request.GroupUnitDto;
import planit.massiverstandard.group.dto.request.ParentGroupUnitDto;
import planit.massiverstandard.group.dto.response.GroupResultDto;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.entity.GroupUnit;
import planit.massiverstandard.group.entity.GroupUnitType;
import planit.massiverstandard.group.repository.GroupRepository;
import planit.massiverstandard.group.service.CommandGroup;
import planit.massiverstandard.group.service.FindGroup;
import planit.massiverstandard.unit.entity.Unit;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GroupInitializer {

    private final GroupRepository groupRepository;
    private final CommandGroup commandGroup;
    private final FindGroup findGroup;

    private static Map<String, GroupResultDto> SAVED_GROUOP_MAP = new HashMap<>();
    private static Map<String, Unit> UNIT_MAP = new HashMap<>();

    private static final Map<String, Map<String, List<String>>> GROUP_UNIT_MAP = new LinkedHashMap<>();

    static {
        GROUP_UNIT_MAP.put("GROUP-A", Map.of(
            "UNIT-A", List.of(),
            "UNIT-B", List.of("UNIT-A")
        ));

        GROUP_UNIT_MAP.put("GROUP-B", Map.of(
            "UNIT-C", List.of(),
            "UNIT-D", List.of(),
            "UNIT-E", List.of("UNIT-C", "UNIT-D"),
            "UNIT-F", List.of("UNIT-E")
        ));

        GROUP_UNIT_MAP.put("GROUP-C", Map.of(
            "UNIT-G", List.of(),
            "UNIT-H", List.of("UNIT-G"),
            "UNIT-I", List.of("UNIT-G"),
            "UNIT-J", List.of("UNIT-H", "UNIT-I")
        ));

        GROUP_UNIT_MAP.put("GROUP-D", Map.of(
            "UNIT-G", List.of(),
            "UNIT-H", List.of("UNIT-G"),
            "UNIT-I", List.of("UNIT-G"),
            "GROUP-A", List.of("UNIT-I", "UNIT-H"),
            "GROUP-B", List.of("GROUP-A"),
            "UNIT-J", List.of("GROUP-A", "GROUP-C")
        ));
    }

    public void init(List<Unit> units) {

        //UUID로 맵 생성
        UNIT_MAP = units.stream()
            .collect(
                Collectors.toMap(Unit::getName, unit -> unit)
            );

        GROUP_UNIT_MAP.forEach((groupName, groupUnitInfo) -> {

            GroupResultDto group = createGroup(groupName);
            SAVED_GROUOP_MAP.put(group.name(), group);
        });

    }

    private List<ParentGroupUnitDto> generateParentGroupUnitDtos(List<String> parentExecutableInfo) {
        return parentExecutableInfo.stream()
            .map(parent -> {
                GroupUnitType type = getType(parent);

                if (type.equals(GroupUnitType.GROUP)) {
                    GroupResultDto groupResultDto = SAVED_GROUOP_MAP.get(parent);
                    Group group = findGroup.byId(groupResultDto.id());
                    return new ParentGroupUnitDto(
                        group.getId(),
                        GroupUnitType.GROUP
                    );
                } else {
                    Unit unit = UNIT_MAP.get(parent);
                    return new ParentGroupUnitDto(
                        unit.getId(),
                        GroupUnitType.UNIT
                    );
                }

            }).toList();
    }

    public GroupResultDto createGroup(String groupName) {

        List<GroupUnitDto> groupUnitDtos = new ArrayList<>();

        // string, List<String> -> GroupUnitDto
        GROUP_UNIT_MAP.get(groupName).forEach((executableName, parentExecutableInfos) -> {
            List<ParentGroupUnitDto> parentGroupUnitDtos = generateParentGroupUnitDtos(parentExecutableInfos);

            GroupUnitType type = getType(executableName);

            GroupUnitDto groupUnitDto;
            if (type.equals(GroupUnitType.GROUP)) {
                GroupResultDto groupResultDto = SAVED_GROUOP_MAP.get(executableName);
                Group group = findGroup.byId(groupResultDto.id());
                groupUnitDto = new GroupUnitDto(
                    group.getId(),
                    GroupUnitType.GROUP,
                    parentGroupUnitDtos
                );
            } else {
                Unit unit = UNIT_MAP.get(executableName);
                groupUnitDto = new GroupUnitDto(
                    unit.getId(),
                    GroupUnitType.UNIT,
                    parentGroupUnitDtos
                );
            }

            groupUnitDtos.add(groupUnitDto);
        });

        GroupDto groupDto = new GroupDto(groupName, groupUnitDtos);

        return commandGroup.create(groupDto);
    }

    private GroupUnitType getType(String name) {
        return name.startsWith("GROUP") ? GroupUnitType.GROUP : GroupUnitType.UNIT;
    }
}
