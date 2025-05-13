package planit.massiverstandard.batch.vo;

import planit.massiverstandard.Executable;

import java.util.List;
import java.util.Map;

public record FlattenResult(
    Map<Executable, List<Executable>> unitGraph,
    Map<Executable, Integer> unitInDegree
) {

}
