package planit.massiverstandard.batch;

import planit.massiverstandard.Executable;
import planit.massiverstandard.unit.entity.Unit;

import java.util.List;
import java.util.Map;

public record FlattenResult(
    Map<Executable, List<Executable>> unitGraph,
    Map<Executable, Integer> unitInDegree
) {

}
