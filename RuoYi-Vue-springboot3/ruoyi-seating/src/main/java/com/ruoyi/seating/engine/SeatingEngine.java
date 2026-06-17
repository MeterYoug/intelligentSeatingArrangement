package com.ruoyi.seating.engine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.domain.SeatRule;
import com.ruoyi.seating.domain.SeatStudent;
import com.ruoyi.seating.domain.SeatStudentRelation;
import com.ruoyi.seating.engine.model.SeatingAssignmentResult;
import com.ruoyi.seating.engine.model.SeatingContext;
import com.ruoyi.seating.engine.model.SeatingResult;
import com.ruoyi.seating.engine.model.SeatingScoreItem;

/**
 * 第一版可解释排座引擎。
 */
@Component
public class SeatingEngine
{
    private static final BigDecimal HARD_PENALTY = BigDecimal.valueOf(10000);

    private static final int DEFAULT_ITERATIONS = 800;

    public SeatingResult generate(SeatingContext context)
    {
        List<SeatStudent> students = new ArrayList<>(context.getStudents());
        List<SeatPosition> seats = new ArrayList<>(context.getSeats());
        List<SeatRule> rules = safeRules(context.getRules());
        List<SeatStudentRelation> relations = safeRelations(context.getRelations());
        students.sort(studentComparator());
        seats.sort(seatComparator());

        Map<Long, Long> frontRowLimits = frontRowLimits(students, rules);
        List<SeatingAssignmentResult> assignments = greedyAssign(students, seats, relations, frontRowLimits);
        assignments = optimize(assignments, relations, frontRowLimits, context);

        List<String> conflicts = hardConflicts(assignments, relations, frontRowLimits);
        List<SeatingScoreItem> scoreItems = score(assignments, relations, frontRowLimits, context.getRandomSeed(), conflicts);
        BigDecimal totalScore = totalScore(scoreItems);
        return new SeatingResult(assignments, scoreItems, totalScore, conflicts);
    }

    public SeatingResult evaluate(List<SeatingAssignmentResult> assignments, List<SeatRule> rules,
            List<SeatStudentRelation> relations, long randomSeed)
    {
        List<SeatRule> safeRules = safeRules(rules);
        List<SeatStudentRelation> safeRelations = safeRelations(relations);
        Map<Long, Long> frontRowLimits = frontRowLimits(
                assignments.stream().map(SeatingAssignmentResult::getStudent).toList(), safeRules);
        List<String> conflicts = hardConflicts(assignments, safeRelations, frontRowLimits);
        List<SeatingScoreItem> scoreItems = score(assignments, safeRelations, frontRowLimits, randomSeed, conflicts);
        BigDecimal totalScore = totalScore(scoreItems);
        return new SeatingResult(assignments, scoreItems, totalScore, conflicts);
    }

    private List<SeatingAssignmentResult> greedyAssign(List<SeatStudent> students, List<SeatPosition> seats,
            List<SeatStudentRelation> relations, Map<Long, Long> frontRowLimits)
    {
        List<SeatingAssignmentResult> assignments = new ArrayList<>();
        Set<Long> usedSeatIds = new HashSet<>();
        BigDecimal averageHeight = averageHeight(students);
        long maxRow = seats.stream().mapToLong(SeatPosition::getRowIndex).max().orElse(1L);

        for (SeatStudent student : students)
        {
            SeatPosition bestSeat = null;
            BigDecimal bestPenalty = null;
            for (SeatPosition seat : seats)
            {
                if (usedSeatIds.contains(seat.getSeatId()))
                {
                    continue;
                }
                BigDecimal penalty = placementPenalty(student, seat, assignments, relations, frontRowLimits,
                        averageHeight, maxRow);
                if (bestPenalty == null || penalty.compareTo(bestPenalty) < 0
                        || (penalty.compareTo(bestPenalty) == 0 && seatComparator().compare(seat, bestSeat) < 0))
                {
                    bestPenalty = penalty;
                    bestSeat = seat;
                }
            }
            usedSeatIds.add(bestSeat.getSeatId());
            assignments.add(new SeatingAssignmentResult(student, bestSeat));
        }
        return assignments;
    }

    private List<SeatingAssignmentResult> optimize(List<SeatingAssignmentResult> assignments,
            List<SeatStudentRelation> relations, Map<Long, Long> frontRowLimits, SeatingContext context)
    {
        int iterations = context.getOptimizeIterations() > 0 ? context.getOptimizeIterations() : DEFAULT_ITERATIONS;
        if (assignments.size() < 2 || iterations <= 0)
        {
            return assignments;
        }

        Random random = new Random(context.getRandomSeed());
        List<SeatingAssignmentResult> best = copyAssignments(assignments);
        BigDecimal bestPenalty = totalPenalty(best, relations, frontRowLimits);
        for (int i = 0; i < iterations; i++)
        {
            int left = random.nextInt(best.size());
            int right = random.nextInt(best.size());
            if (left == right)
            {
                continue;
            }
            List<SeatingAssignmentResult> candidate = copyAssignments(best);
            SeatPosition leftSeat = candidate.get(left).getSeat();
            candidate.get(left).setSeat(candidate.get(right).getSeat());
            candidate.get(right).setSeat(leftSeat);
            BigDecimal candidatePenalty = totalPenalty(candidate, relations, frontRowLimits);
            if (candidatePenalty.compareTo(bestPenalty) < 0)
            {
                best = candidate;
                bestPenalty = candidatePenalty;
            }
        }
        return best;
    }

    private BigDecimal placementPenalty(SeatStudent student, SeatPosition seat,
            List<SeatingAssignmentResult> assignments, List<SeatStudentRelation> relations,
            Map<Long, Long> frontRowLimits, BigDecimal averageHeight, long maxRow)
    {
        BigDecimal penalty = softPlacementPenalty(student, seat, assignments, averageHeight, maxRow);
        Long frontRowLimit = frontRowLimits.get(student.getStudentId());
        if (frontRowLimit != null && seat.getRowIndex() > frontRowLimit)
        {
            penalty = penalty.add(HARD_PENALTY);
        }
        for (SeatingAssignmentResult assigned : assignments)
        {
            SeatStudentRelation relation = findRelation(relations, student.getStudentId(),
                    assigned.getStudent().getStudentId());
            if (relation == null)
            {
                continue;
            }
            if ("NOT_DESKMATE".equals(relation.getRelationType()) && sameDesk(seat, assigned.getSeat()))
            {
                penalty = penalty.add(HARD_PENALTY);
            }
            if ("NOT_ADJACENT".equals(relation.getRelationType()) && adjacent(seat, assigned.getSeat()))
            {
                penalty = penalty.add(HARD_PENALTY);
            }
        }
        return penalty;
    }

    private BigDecimal softPlacementPenalty(SeatStudent student, SeatPosition seat,
            List<SeatingAssignmentResult> assignments, BigDecimal averageHeight, long maxRow)
    {
        BigDecimal penalty = BigDecimal.ZERO;
        int visionLevel = intValue(student.getVisionLevel());
        if (visionLevel > 0)
        {
            penalty = penalty.add(BigDecimal.valueOf((seat.getRowIndex() - 1) * visionLevel * 4L));
        }
        if (student.getHeightCm() != null && averageHeight.compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal heightDiff = student.getHeightCm().subtract(averageHeight);
            if (heightDiff.compareTo(BigDecimal.valueOf(5)) >= 0)
            {
                penalty = penalty.add(BigDecimal.valueOf(maxRow - seat.getRowIndex()).multiply(BigDecimal.valueOf(2)));
            }
            else if (heightDiff.compareTo(BigDecimal.valueOf(-5)) <= 0)
            {
                penalty = penalty.add(BigDecimal.valueOf(seat.getRowIndex() - 1));
            }
        }
        int disciplineLevel = intValue(student.getDisciplineLevel());
        if (disciplineLevel > 0)
        {
            long adjacentDisciplineCount = assignments.stream()
                    .filter(item -> intValue(item.getStudent().getDisciplineLevel()) > 0)
                    .filter(item -> adjacent(item.getSeat(), seat))
                    .count();
            penalty = penalty.add(BigDecimal.valueOf(adjacentDisciplineCount * disciplineLevel * 20L));
        }
        return penalty;
    }

    private BigDecimal totalPenalty(List<SeatingAssignmentResult> assignments, List<SeatStudentRelation> relations,
            Map<Long, Long> frontRowLimits)
    {
        BigDecimal penalty = BigDecimal.ZERO;
        BigDecimal averageHeight = averageHeight(assignments.stream().map(SeatingAssignmentResult::getStudent).toList());
        long maxRow = assignments.stream().mapToLong(item -> item.getSeat().getRowIndex()).max().orElse(1L);
        for (SeatingAssignmentResult assignment : assignments)
        {
            penalty = penalty.add(softPlacementPenalty(assignment.getStudent(), assignment.getSeat(),
                    assignmentsBefore(assignments, assignment), averageHeight, maxRow));
            Long frontRowLimit = frontRowLimits.get(assignment.getStudent().getStudentId());
            if (frontRowLimit != null && assignment.getSeat().getRowIndex() > frontRowLimit)
            {
                penalty = penalty.add(HARD_PENALTY);
            }
        }
        for (SeatStudentRelation relation : relations)
        {
            SeatingAssignmentResult left = findAssignment(assignments, relation.getStudentId());
            SeatingAssignmentResult right = findAssignment(assignments, relation.getRelatedId());
            if (left == null || right == null)
            {
                continue;
            }
            if ("NOT_DESKMATE".equals(relation.getRelationType()) && sameDesk(left.getSeat(), right.getSeat()))
            {
                penalty = penalty.add(HARD_PENALTY);
            }
            if ("NOT_ADJACENT".equals(relation.getRelationType()) && adjacent(left.getSeat(), right.getSeat()))
            {
                penalty = penalty.add(HARD_PENALTY);
            }
            if ("PREFER_DESKMATE".equals(relation.getRelationType()) && !sameDesk(left.getSeat(), right.getSeat()))
            {
                penalty = penalty.add(BigDecimal.valueOf(weight(relation)));
            }
        }
        return penalty;
    }

    private List<SeatingScoreItem> score(List<SeatingAssignmentResult> assignments,
            List<SeatStudentRelation> relations, Map<Long, Long> frontRowLimits, long randomSeed, List<String> conflicts)
    {
        List<SeatingScoreItem> scoreItems = new ArrayList<>();
        scoreItems.add(scoreFrontRows(assignments, frontRowLimits));
        scoreItems.add(scoreVision(assignments));
        scoreItems.add(scoreHeight(assignments));
        scoreItems.add(scoreGender(assignments));
        scoreItems.add(scoreScoreLevel(assignments));
        scoreItems.add(scoreDiscipline(assignments));
        scoreItems.add(scoreRelations(assignments, relations));
        scoreItems.add(scoreCapacity(assignments));
        scoreItems.add(scoreSeed(randomSeed, conflicts));
        return scoreItems;
    }

    private SeatingScoreItem scoreFrontRows(List<SeatingAssignmentResult> assignments, Map<Long, Long> frontRowLimits)
    {
        int required = 0;
        int violations = 0;
        for (SeatingAssignmentResult assignment : assignments)
        {
            Long frontRowLimit = frontRowLimits.get(assignment.getStudent().getStudentId());
            if (frontRowLimit == null)
            {
                continue;
            }
            required++;
            if (assignment.getSeat().getRowIndex() > frontRowLimit)
            {
                violations++;
            }
        }
        return scoreItem("FRONT_ROW", "指定学生坐前排", BigDecimal.valueOf(violations * 100L),
                Map.of("required", required, "violations", violations));
    }

    private SeatingScoreItem scoreVision(List<SeatingAssignmentResult> assignments)
    {
        BigDecimal penalty = BigDecimal.ZERO;
        int affected = 0;
        for (SeatingAssignmentResult assignment : assignments)
        {
            int visionLevel = intValue(assignment.getStudent().getVisionLevel());
            if (visionLevel > 0)
            {
                affected++;
                penalty = penalty.add(BigDecimal.valueOf((assignment.getSeat().getRowIndex() - 1) * visionLevel));
            }
        }
        return scoreItem("VISION_FRONT", "近视学生靠前", penalty, Map.of("affected", affected));
    }

    private SeatingScoreItem scoreHeight(List<SeatingAssignmentResult> assignments)
    {
        BigDecimal averageHeight = averageHeight(assignments.stream().map(SeatingAssignmentResult::getStudent).toList());
        long maxRow = assignments.stream().mapToLong(item -> item.getSeat().getRowIndex()).max().orElse(1L);
        BigDecimal penalty = BigDecimal.ZERO;
        int affected = 0;
        for (SeatingAssignmentResult assignment : assignments)
        {
            SeatStudent student = assignment.getStudent();
            if (student.getHeightCm() == null || averageHeight.compareTo(BigDecimal.ZERO) == 0)
            {
                continue;
            }
            BigDecimal heightDiff = student.getHeightCm().subtract(averageHeight);
            if (heightDiff.compareTo(BigDecimal.valueOf(5)) >= 0)
            {
                affected++;
                penalty = penalty.add(BigDecimal.valueOf(maxRow - assignment.getSeat().getRowIndex()));
            }
        }
        return scoreItem("HEIGHT_BACK", "高个学生靠后", penalty, Map.of("affected", affected));
    }

    private SeatingScoreItem scoreGender(List<SeatingAssignmentResult> assignments)
    {
        int pairs = 0;
        int sameGenderPairs = 0;
        for (int i = 0; i < assignments.size(); i++)
        {
            for (int j = i + 1; j < assignments.size(); j++)
            {
                if (!sameDesk(assignments.get(i).getSeat(), assignments.get(j).getSeat()))
                {
                    continue;
                }
                String leftGender = assignments.get(i).getStudent().getGender();
                String rightGender = assignments.get(j).getStudent().getGender();
                if (!"2".equals(leftGender) && !"2".equals(rightGender))
                {
                    pairs++;
                    if (leftGender != null && leftGender.equals(rightGender))
                    {
                        sameGenderPairs++;
                    }
                }
            }
        }
        return scoreItem("GENDER_BALANCE", "男女搭配均衡", BigDecimal.valueOf(sameGenderPairs * 5L),
                Map.of("pairs", pairs, "sameGenderPairs", sameGenderPairs));
    }

    private SeatingScoreItem scoreScoreLevel(List<SeatingAssignmentResult> assignments)
    {
        int pairs = 0;
        int sameLevelPairs = 0;
        for (int i = 0; i < assignments.size(); i++)
        {
            for (int j = i + 1; j < assignments.size(); j++)
            {
                if (!sameDesk(assignments.get(i).getSeat(), assignments.get(j).getSeat()))
                {
                    continue;
                }
                String leftScore = assignments.get(i).getStudent().getScoreLevel();
                String rightScore = assignments.get(j).getStudent().getScoreLevel();
                if (leftScore != null && rightScore != null)
                {
                    pairs++;
                    if (leftScore.equals(rightScore))
                    {
                        sameLevelPairs++;
                    }
                }
            }
        }
        return scoreItem("SCORE_BALANCE", "成绩强弱均衡", BigDecimal.valueOf(sameLevelPairs * 5L),
                Map.of("pairs", pairs, "sameLevelPairs", sameLevelPairs));
    }

    private SeatingScoreItem scoreDiscipline(List<SeatingAssignmentResult> assignments)
    {
        int adjacentPairs = 0;
        for (int i = 0; i < assignments.size(); i++)
        {
            for (int j = i + 1; j < assignments.size(); j++)
            {
                if (intValue(assignments.get(i).getStudent().getDisciplineLevel()) > 0
                        && intValue(assignments.get(j).getStudent().getDisciplineLevel()) > 0
                        && adjacent(assignments.get(i).getSeat(), assignments.get(j).getSeat()))
                {
                    adjacentPairs++;
                }
            }
        }
        return scoreItem("DISCIPLINE_SCATTER", "纪律关注学生分散", BigDecimal.valueOf(adjacentPairs * 10L),
                Map.of("adjacentPairs", adjacentPairs));
    }

    private SeatingScoreItem scoreRelations(List<SeatingAssignmentResult> assignments, List<SeatStudentRelation> relations)
    {
        int violations = 0;
        int preferredMissed = 0;
        for (SeatStudentRelation relation : relations)
        {
            SeatingAssignmentResult left = findAssignment(assignments, relation.getStudentId());
            SeatingAssignmentResult right = findAssignment(assignments, relation.getRelatedId());
            if (left == null || right == null)
            {
                continue;
            }
            if ("NOT_DESKMATE".equals(relation.getRelationType()) && sameDesk(left.getSeat(), right.getSeat()))
            {
                violations++;
            }
            if ("NOT_ADJACENT".equals(relation.getRelationType()) && adjacent(left.getSeat(), right.getSeat()))
            {
                violations++;
            }
            if ("PREFER_DESKMATE".equals(relation.getRelationType()) && !sameDesk(left.getSeat(), right.getSeat()))
            {
                preferredMissed++;
            }
        }
        BigDecimal penalty = BigDecimal.valueOf(violations * 100L + preferredMissed * 10L);
        return scoreItem("STUDENT_RELATION", "学生关系约束", penalty,
                Map.of("violations", violations, "preferredMissed", preferredMissed));
    }

    private SeatingScoreItem scoreCapacity(List<SeatingAssignmentResult> assignments)
    {
        return scoreItem("CAPACITY", "学生座位匹配", BigDecimal.ZERO, Map.of("assigned", assignments.size()));
    }

    private SeatingScoreItem scoreSeed(long randomSeed, List<String> conflicts)
    {
        return scoreItem("RANDOM_SEED", "固定随机种子", BigDecimal.ZERO,
                Map.of("seed", randomSeed, "conflictCount", conflicts.size()));
    }

    private SeatingScoreItem scoreItem(String ruleCode, String ruleName, BigDecimal penalty, Map<String, Object> detail)
    {
        BigDecimal score = BigDecimal.valueOf(100).subtract(penalty);
        if (score.compareTo(BigDecimal.ZERO) < 0)
        {
            score = BigDecimal.ZERO;
        }
        Map<String, Object> detailMap = new HashMap<>(detail);
        detailMap.put("penalty", penalty);
        return new SeatingScoreItem(ruleCode, ruleName, score.setScale(2, RoundingMode.HALF_UP),
                penalty.setScale(2, RoundingMode.HALF_UP), JSON.toJSONString(detailMap));
    }

    private BigDecimal totalScore(List<SeatingScoreItem> scoreItems)
    {
        if (scoreItems.isEmpty())
        {
            return BigDecimal.ZERO;
        }
        return scoreItems.stream()
                .map(SeatingScoreItem::getScoreValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(scoreItems.size()), 2, RoundingMode.HALF_UP);
    }

    private List<String> hardConflicts(List<SeatingAssignmentResult> assignments,
            List<SeatStudentRelation> relations, Map<Long, Long> frontRowLimits)
    {
        List<String> conflicts = new ArrayList<>();
        for (SeatingAssignmentResult assignment : assignments)
        {
            Long frontRowLimit = frontRowLimits.get(assignment.getStudent().getStudentId());
            if (frontRowLimit != null && assignment.getSeat().getRowIndex() > frontRowLimit)
            {
                conflicts.add(assignment.getStudent().getStudentName() + " 未能安排在前 " + frontRowLimit + " 排");
            }
        }
        for (SeatStudentRelation relation : relations)
        {
            SeatingAssignmentResult left = findAssignment(assignments, relation.getStudentId());
            SeatingAssignmentResult right = findAssignment(assignments, relation.getRelatedId());
            if (left == null || right == null)
            {
                continue;
            }
            if ("NOT_DESKMATE".equals(relation.getRelationType()) && sameDesk(left.getSeat(), right.getSeat()))
            {
                conflicts.add(left.getStudent().getStudentName() + " 与 " + right.getStudent().getStudentName() + " 不能同桌");
            }
            if ("NOT_ADJACENT".equals(relation.getRelationType()) && adjacent(left.getSeat(), right.getSeat()))
            {
                conflicts.add(left.getStudent().getStudentName() + " 与 " + right.getStudent().getStudentName() + " 不能相邻");
            }
        }
        return conflicts;
    }

    private Map<Long, Long> frontRowLimits(List<SeatStudent> students, List<SeatRule> rules)
    {
        Map<Long, Long> limits = new HashMap<>();
        for (SeatStudent student : students)
        {
            if (student.getSpecialNeed() != null && student.getSpecialNeed().contains("前排"))
            {
                limits.put(student.getStudentId(), 2L);
            }
        }
        for (SeatRule rule : rules)
        {
            if (!"FRONT_ROW".equals(rule.getRuleCode()) && !"MUST_FRONT_ROW".equals(rule.getRuleCode())
                    && !"STUDENT_FRONT_ROW".equals(rule.getRuleCode()))
            {
                continue;
            }
            JSONObject config = parseConfig(rule.getRuleConfig());
            if (config == null)
            {
                continue;
            }
            Long configuredFrontRows = config.getLong("frontRows");
            long frontRows = configuredFrontRows == null ? 2L : configuredFrontRows;
            JSONArray studentIds = config.getJSONArray("studentIds");
            if (studentIds == null)
            {
                Long studentId = config.getLong("studentId");
                if (studentId != null)
                {
                    limits.put(studentId, frontRows);
                }
                continue;
            }
            for (int i = 0; i < studentIds.size(); i++)
            {
                Long studentId = studentIds.getLong(i);
                if (studentId != null)
                {
                    limits.put(studentId, frontRows);
                }
            }
        }
        return limits;
    }

    private JSONObject parseConfig(String ruleConfig)
    {
        try
        {
            return ruleConfig == null || ruleConfig.isBlank() ? null : JSON.parseObject(ruleConfig);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private List<SeatingAssignmentResult> assignmentsBefore(List<SeatingAssignmentResult> assignments,
            SeatingAssignmentResult target)
    {
        List<SeatingAssignmentResult> before = new ArrayList<>();
        for (SeatingAssignmentResult assignment : assignments)
        {
            if (assignment == target)
            {
                break;
            }
            before.add(assignment);
        }
        return before;
    }

    private List<SeatingAssignmentResult> copyAssignments(List<SeatingAssignmentResult> assignments)
    {
        List<SeatingAssignmentResult> copied = new ArrayList<>();
        for (SeatingAssignmentResult assignment : assignments)
        {
            copied.add(new SeatingAssignmentResult(assignment.getStudent(), assignment.getSeat()));
        }
        return copied;
    }

    private SeatingAssignmentResult findAssignment(List<SeatingAssignmentResult> assignments, Long studentId)
    {
        if (studentId == null)
        {
            return null;
        }
        return assignments.stream()
                .filter(item -> studentId.equals(item.getStudent().getStudentId()))
                .findFirst()
                .orElse(null);
    }

    private SeatStudentRelation findRelation(List<SeatStudentRelation> relations, Long leftStudentId, Long rightStudentId)
    {
        for (SeatStudentRelation relation : relations)
        {
            if (samePair(relation, leftStudentId, rightStudentId))
            {
                return relation;
            }
        }
        return null;
    }

    private boolean samePair(SeatStudentRelation relation, Long leftStudentId, Long rightStudentId)
    {
        return relation.getStudentId() != null && relation.getRelatedId() != null
                && ((relation.getStudentId().equals(leftStudentId) && relation.getRelatedId().equals(rightStudentId))
                || (relation.getStudentId().equals(rightStudentId) && relation.getRelatedId().equals(leftStudentId)));
    }

    private BigDecimal averageHeight(List<SeatStudent> students)
    {
        List<BigDecimal> heights = students.stream()
                .map(SeatStudent::getHeightCm)
                .filter(height -> height != null)
                .toList();
        if (heights.isEmpty())
        {
            return BigDecimal.ZERO;
        }
        return heights.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(heights.size()), 2, RoundingMode.HALF_UP);
    }

    private boolean sameDesk(SeatPosition left, SeatPosition right)
    {
        if (!left.getRowIndex().equals(right.getRowIndex()) || Math.abs(left.getColIndex() - right.getColIndex()) != 1)
        {
            return false;
        }
        return Math.min(left.getColIndex(), right.getColIndex()) % 2 == 1;
    }

    private boolean adjacent(SeatPosition left, SeatPosition right)
    {
        long rowDistance = Math.abs(left.getRowIndex() - right.getRowIndex());
        long colDistance = Math.abs(left.getColIndex() - right.getColIndex());
        return rowDistance + colDistance == 1;
    }

    private List<SeatRule> safeRules(List<SeatRule> rules)
    {
        return rules == null ? new ArrayList<>() : rules;
    }

    private List<SeatStudentRelation> safeRelations(List<SeatStudentRelation> relations)
    {
        if (relations == null)
        {
            return new ArrayList<>();
        }
        return relations.stream()
                .filter(relation -> "1".equals(relation.getEnabled()))
                .toList();
    }

    private long weight(SeatStudentRelation relation)
    {
        return relation.getRelationWeight() == null ? 100L : relation.getRelationWeight();
    }

    private Comparator<SeatStudent> studentComparator()
    {
        return Comparator
                .comparing((SeatStudent student) -> intValue(student.getDisciplineLevel())).reversed()
                .thenComparing((SeatStudent student) -> intValue(student.getVisionLevel()), Comparator.reverseOrder())
                .thenComparing(SeatStudent::getSortNo, Comparator.nullsLast(Long::compareTo))
                .thenComparing(SeatStudent::getStudentNo, Comparator.nullsLast(String::compareTo))
                .thenComparing(SeatStudent::getStudentId, Comparator.nullsLast(Long::compareTo));
    }

    private Comparator<SeatPosition> seatComparator()
    {
        return Comparator
                .comparing(SeatPosition::getRowIndex, Comparator.nullsLast(Long::compareTo))
                .thenComparing(SeatPosition::getColIndex, Comparator.nullsLast(Long::compareTo))
                .thenComparing(SeatPosition::getSeatId, Comparator.nullsLast(Long::compareTo));
    }

    private int intValue(String value)
    {
        try
        {
            return value == null || value.isBlank() ? 0 : Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }
}
