package planit.massiverstandard.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import planit.massiverstandard.Executable;
import planit.massiverstandard.batch.job.BatchJob;
import planit.massiverstandard.group.entity.Group;
import planit.massiverstandard.group.entity.GroupUnit;
import planit.massiverstandard.group.service.FindGroup;
import planit.massiverstandard.group.service.GroupGetService;
import planit.massiverstandard.group.service.GroupService;
import planit.massiverstandard.unit.entity.Unit;
import planit.massiverstandard.unit.service.UnitGetService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobLauncher {

    private final UnitGetService unitGetService;
    private final JobLauncher jobLauncher;
    private final GroupService groupService;

    private final BatchJob batchJob;
    private final GroupGetService groupGetService;

    private final FindGroup findGroup;

    private Executable getExecutableByType(GroupUnit groupUnit) {
        return groupUnit.isChildGroup() ? groupUnit.getChildGroup() : groupUnit.getChildUnit();
    }

    private void flatten(Group group, List<Executable> visitedGroup, Map<Executable, List<Executable>> graph, Map<Executable, Integer> inDegree, Map<UUID, List<Executable>> subGraph) {

        if (visitedGroup.contains(group)) {
            return;
        }

        visitedGroup.add(group);

        for (GroupUnit groupUnit : group.getGroupUnits()) {

            Executable executable = getExecutableByType(groupUnit);

            graph.putIfAbsent(executable, new ArrayList<>());
            inDegree.putIfAbsent(executable, 0);

            // subGraph에 GroupUnit 추가
            subGraph.putIfAbsent(group.getId(), new ArrayList<>());
            subGraph.get(group.getId()).add(executable);

            for (GroupUnit parent : groupUnit.getParentGroupUnits()) {
                Executable parentExecutable = getExecutableByType(parent);

                graph.putIfAbsent(parentExecutable, new ArrayList<>());
                graph.get(parentExecutable).add(executable);
                inDegree.put(executable, inDegree.getOrDefault(executable, 0) + 1);
            }
        }

        // 2️⃣ 순회하며 Group이면 하위 Group을 평탄화
        List<Executable> currentExecutables = new ArrayList<>(graph.keySet());
        for (Executable executable : currentExecutables) {
            if (executable instanceof Group childGroup) {
                flatten(childGroup, visitedGroup, graph, inDegree, subGraph);
            }
        }

    }

    private void postProcess(List<Executable> visitedGroup, Map<Executable, List<Executable>> graph, Map<Executable, Integer> inDegree, Map<UUID, List<Executable>> subGraph) {
        // 3️⃣ 평탄화된 Group 정보로 group의 부모 Unit과 연결
        // Group을 제거하고 해당 Group의 자식들을 Group의 부모와 직접 연결
        List<Executable> groupsToRemove = new ArrayList<>();

        // 1️⃣ keySet 복사본으로 루프
        List<Executable> allNodes = new ArrayList<>(graph.keySet());

        for (Executable executable : allNodes) {
            if (executable instanceof Group) {

                groupsToRemove.add(executable); // Group 노드 제거를 위한 리스트에 추가

                // Group의 부모들 찾기
                List<Executable> parents = new ArrayList<>();
                for (Executable potentialParent : new ArrayList<>(graph.keySet())) {
                    List<Executable> executables = graph.get(potentialParent);
                    if (executables.contains(executable)) {
                        parents.add(potentialParent);

                        // Group 부모 graph에서 제거
                        executables.remove(executable); // 부모에서 Group 제거
                    }
                }

                // subGraph에서 Group의 자식들 찾기
                List<Executable> childExecutables = subGraph.get(executable.getId());
                List<Executable> rootChildrenToConnect = childExecutables.stream()
                    .filter(child -> inDegree.get(child) == 0) // 차수가 0인 것들만
                    .toList();

                // Group의 자식들 중 차수가 0인 것들만 부모와 연결
                for (Executable parent : parents) {
                    for (Executable child : rootChildrenToConnect) {
                        if (!graph.get(parent).contains(child)) { // 중복 방지
                            graph.get(parent).add(child); // 부모에 자식 추가
                            inDegree.put(child, inDegree.get(child) + 1); // 자식의 진입 차수 증가
                        }
                    }
                }

                // 자식들 중 자식이 없는 Executable들 찾기
                List<Executable> lastExecutables = childExecutables.stream()
                    .filter(child -> graph.get(child).isEmpty()).toList();

                // 2️⃣ postExecutables 복사본 생성
                List<Executable> postExecutables = new ArrayList<>(graph.getOrDefault(executable, List.of()));

                // Group → 후행 노드 연결 재조정
                for (Executable post : postExecutables) {
                    // 2‑1. 기존 간선 제거
                    graph.get(executable).remove(post);

                    // 2‑2. inDegree 조정
                    int deg = inDegree.getOrDefault(post, 0) - 1 + lastExecutables.size();
                    inDegree.put(post, deg);
                }

                // Group의 마지막 Executable의 graph 업데이트
                for (Executable last : lastExecutables) {
                    for (Executable post : postExecutables) {
                        if (!graph.get(last).contains(post)) { // 중복 방지
                            graph.get(last).add(post); // 마지막 Executable에 후행 Executable 추가
                        }
                    }
                }
            }
        }

        // Group 노드 제거
        for (Executable groupToRemove : groupsToRemove) {
            graph.remove(groupToRemove);
            inDegree.remove(groupToRemove);
        }
    }

    @Transactional
    public void runGroup(UUID groupId) {
        Group group = findGroup.byId(groupId);

        List<Executable> visitedGroup = new ArrayList<>();
        Map<Executable, List<Executable>> graph = new HashMap<>();
        Map<Executable, Integer> inDegree = new HashMap<>();
        Map<UUID, List<Executable>> subGraph = new HashMap<>();

        try {
            flatten(group, visitedGroup, graph, inDegree, subGraph);
            postProcess(visitedGroup, graph, inDegree, subGraph);
        } catch (Exception e) {
            log.error("Error during flattening and post-processing: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // ─── 보기 좋게 포맷팅해서 로깅 ───────────────────────────
        log.info("===== 최종 Graph 구조 =====\n{}", prettyGraph(graph));
        log.info("===== 최종 InDegree Map =====\n{}", prettyInDegree(inDegree));
        // ─────────────────────────────────────────────────────────
        runFlattenedGraph(graph, inDegree);
    }

    private String prettyGraph(Map<Executable, List<Executable>> graph) {
        return graph.entrySet().stream()
            .map(entry -> {
                String nodeDesc = describe(entry.getKey());
                String childrenDesc = entry.getValue().stream()
                    .map(this::describe)
                    .collect(Collectors.joining(", "));
                return String.format("  %s → [%s]", nodeDesc, childrenDesc);
            })
            .collect(Collectors.joining("\n"));
    }

    private String prettyInDegree(Map<Executable, Integer> inDegree) {
        return inDegree.entrySet().stream()
            .map(entry -> String.format("  %s = %d", describe(entry.getKey()), entry.getValue()))
            .collect(Collectors.joining("\n"));
    }

    // Executable 이 Group/Unit 인스턴스이므로,
// 좀 더 읽기 쉽도록 타입별로 이름·ID를 붙여줍니다.
    private String describe(Executable exec) {
        if (exec instanceof Group g) {
            return "Group[" + g.getName() + "]";
        }
        if (exec instanceof Unit u) {
            return "Unit[" + u.getName() + "]";
        }
        return exec.getId().toString();
    }

    private void runFlattenedGraph(
        Map<Executable, List<Executable>> graph,
        Map<Executable, Integer> inDegree
    ) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Set<Executable> completed = ConcurrentHashMap.newKeySet();
        Queue<Executable> ready = new LinkedList<>();

        // 0️⃣ 초기 준비 노드 로깅
        for (Executable e : inDegree.keySet()) {
            if (inDegree.get(e) == 0) ready.add(e);
        }
        log.info("초기 준비 노드: {}", describeList(ready));

        int batchNo = 1;
        while (!ready.isEmpty()) {
            // 1️⃣ 이번 배치에 병렬 실행할 노드 집합
            List<Executable> parallel = new ArrayList<>();
            while (!ready.isEmpty()) parallel.add(ready.poll());
            log.info("=== 배치 #{} 시작 – 병렬 실행 대상 ({}개) ===", batchNo, parallel.size());
            log.info("  ▶ 대상 노드: {}", describeList(parallel));

            // 2️⃣ 병렬 실행
            List<Future<?>> futures = parallel.stream()
                .map(e -> executor.submit(() -> {
                    String desc = describeExcute(e);
                    log.info("[START] {}", desc);
                    if (e instanceof Unit unit) {
                        try {
                            runBatchJob(unit.getId());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    log.info("[END]   {}", desc);
                    completed.add(e);
                }))
                .collect(Collectors.toList());

            // 3️⃣ 모든 병렬 작업 완료 대기
            int finalBatchNo = batchNo;
            futures.forEach(f -> {
                try {
                    f.get();
                } catch (Exception ex) {
                    log.error("배치 #{} 실행 오류", finalBatchNo, ex);
                }
            });

            // 4️⃣ 의존 해제 후 다음 준비 노드 수집
            List<Executable> nextReady = new ArrayList<>();
            for (Executable e : parallel) {
                for (Executable dep : graph.getOrDefault(e, List.of())) {
                    int newDeg = inDegree.merge(dep, -1, Integer::sum);
                    if (newDeg == 0) nextReady.add(dep);
                }
            }
            log.info("배치 #{} 완료 – 총 완료 노드: {}", batchNo, describeList(completed));
            log.info("배치 #{} 후 다음 준비 노드: {}", batchNo, describeList(nextReady));

            // 5️⃣ 준비 큐에 다음 노드 등록
            ready.addAll(nextReady);
            batchNo++;
        }

        executor.shutdown();
        log.info("■ 전체 실행 완료 – 총 {}개 노드 처리", completed.size());
    }

    // — 헬퍼 메서드들 —
    private String describeExcute(Executable exec) {
        if (exec instanceof Group g) {
            return String.format("Group[%s](%s)", g.getName(), g.getId());
        }
        if (exec instanceof Unit u) {
            return String.format("Unit[%s](%s)", u.getName(), u.getId());
        }
        return exec.getId().toString();
    }

    private String describeList(Collection<Executable> list) {
        return list.stream()
            .map(this::describe)
            .collect(Collectors.joining(", "));
    }

//    @Transactional
//    public void runGroup(UUID groupId) {
//        // 1️⃣ DAG 그래프 생성 (위상 정렬을 위한 진입 차수 관리)
//        Map<Unit, List<Unit>> graph = new HashMap<>();
//        Map<Unit, Integer> inDegree = new HashMap<>();
//        Set<Unit> completedUnits = new HashSet<>();
//
//        Group group = groupGetService.byId(groupId);
//        List<GroupUnit> groupUnits = group.getGroupUnits();
//
//        for (GroupUnit groupUnit : groupUnits) {
//            Unit unit = groupUnit.getChildUnit();
//            graph.putIfAbsent(unit, new ArrayList<>());
//            inDegree.putIfAbsent(unit, 0);
//
//            for (GroupUnit parent : groupUnit.getParentGroupUnits()) {
//                Unit parentUnit = parent.getChildUnit();
//                graph.putIfAbsent(parentUnit, new ArrayList<>());
//                graph.get(parentUnit).add(unit);
//                inDegree.put(unit, inDegree.getOrDefault(unit, 0) + 1);
//            }
//        }
//
//        // 2️⃣ 위상 정렬 (실행 순서 결정)
//        Queue<Unit> readyQueue = new LinkedList<>();
//        for (Unit unit : inDegree.keySet()) {
//            if (inDegree.get(unit) == 0) {
//                readyQueue.add(unit);
//            }
//        }
//
//        // 3️⃣ DAG 기반 병렬 실행 처리
//        ExecutorService executor = Executors.newFixedThreadPool(10);
//
//        while (!readyQueue.isEmpty()) {
//            List<Unit> parallelUnits = new ArrayList<>();
//
//            // 병렬 실행 가능한 Unit들 가져오기
//            while (!readyQueue.isEmpty()) {
//                parallelUnits.add(readyQueue.poll());
//            }
//
//            List<Future<?>> futures = new ArrayList<>();
//
//            // 병렬 실행
//            for (Unit unit : parallelUnits) {
//                futures.add(executor.submit(() -> {
//                    try {
//                        runBatchJob(unit.getId());
//                        synchronized (completedUnits) {
//                            completedUnits.add(unit);
//                        }
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }));
//            }
//
//            // 모든 병렬 실행 완료 대기
//            for (Future<?> future : futures) {
//                try {
//                    future.get();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            // 4️⃣ 실행 완료된 Unit 기반으로 다음 실행할 Unit 추가
//            for (Unit unit : parallelUnits) {
//                for (Unit dependent : graph.getOrDefault(unit, new ArrayList<>())) {
//                    inDegree.put(dependent, inDegree.get(dependent) - 1);
//                    if (inDegree.get(dependent) == 0) {
//                        readyQueue.add(dependent);
//                    }
//                }
//            }
//        }
//
//        executor.shutdown();
//    }


//    private void executeParallelUnits(List<Unit> units) {
//        ExecutorService executor = Executors.newFixedThreadPool(units.size());
//        List<Future<?>> futures = new ArrayList<>();
//
//        for (Unit unit : units) {
//            futures.add(executor.submit(() -> {
//                try {
//                    runBatchJob(unit.getId());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }));
//        }
//
//        // 모든 병렬 실행이 완료될 때까지 대기
//        for (Future<?> future : futures) {
//            try {
//                future.get();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        executor.shutdown();
//    }

    public void runBatchJob(UUID unitId) throws Exception {
        Unit unit = unitGetService.byId(unitId);
        JobParameters jobParameters = getJobParameters(unit);

        Job job = batchJob.createJob("JOB_" + unit.getId());
        jobLauncher.run(job, jobParameters);
    }

    private JobParameters getJobParameters(Unit unit) {
        return new JobParametersBuilder()
            .addString("unitId", unit.getId().toString())
            .addLong("time", System.currentTimeMillis()) // 중복 실행 방지
            .toJobParameters();
    }

}
