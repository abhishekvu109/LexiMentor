package com.abhi.leximentor.fitmate.mapper;

import com.abhi.leximentor.fitmate.constants.DifficultyLevel;
import com.abhi.leximentor.fitmate.constants.RiskLevel;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.constants.Unit;
import com.abhi.leximentor.fitmate.util.FitmateUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Shared named conversion methods used by all MapStruct mappers via {@code uses = {FitmateMapperHelper.class}}.
 *
 * <p>Each method is annotated with {@code @Named} so mappers can reference it with
 * {@code qualifiedByName = "..."} in their {@code @Mapping} annotations.</p>
 *
 * <p>All methods are pure functions — no side effects, no repository calls.</p>
 */
@Component
public class FitmateMapperHelper {

    // -----------------------------------------------------------------------
    // Application Status  (int <-> String)
    // -----------------------------------------------------------------------

    @Named("applicationStatusToString")
    public String applicationStatusToString(int status) {
        return Status.ApplicationStatus.getStatusStr(status);
    }

    /**
     * Converts a status String to the int constant. Defaults to ACTIVE when blank.
     */
    @Named("applicationStatusToInt")
    public int applicationStatusToInt(String status) {
        if (StringUtils.isBlank(status)) {
            return Status.ApplicationStatus.ACTIVE;
        }
        return Status.ApplicationStatus.getStatus(status);
    }

    // -----------------------------------------------------------------------
    // Routine Status  (int <-> String)
    // -----------------------------------------------------------------------

    @Named("routineStatusToString")
    public String routineStatusToString(int status) {
        return Status.RoutineStatus.toString(status);
    }

    // -----------------------------------------------------------------------
    // Difficulty Level  (int <-> String)
    // -----------------------------------------------------------------------

    @Named("difficultyLevelToString")
    public String difficultyLevelToString(int score) {
        return DifficultyLevel.parse(score).toString();
    }

    @Named("difficultyLevelToInt")
    public int difficultyLevelToInt(String level) {
        return DifficultyLevel.parse(level).getScore();
    }

    // -----------------------------------------------------------------------
    // Risk Level  (int <-> String)
    // -----------------------------------------------------------------------

    @Named("riskLevelToString")
    public String riskLevelToString(int score) {
        return RiskLevel.parse(score).toString();
    }

    @Named("riskLevelToInt")
    public int riskLevelToInt(String level) {
        return RiskLevel.parse(level).getScore();
    }

    // -----------------------------------------------------------------------
    // Unit  (String normalisation)
    // -----------------------------------------------------------------------

    /**
     * Normalises a raw unit string through the {@link Unit} enum so it is
     * always stored as a clean lowercase value (e.g. "weight", "reps", "time").
     */
    @Named("unitFromString")
    public String unitFromString(String unit) {
        return Unit.from(unit).getValue();
    }

    // -----------------------------------------------------------------------
    // ID helpers  (long <-> String)
    // -----------------------------------------------------------------------

    @Named("longToString")
    public String longToString(long value) {
        return String.valueOf(value);
    }

    // -----------------------------------------------------------------------
    // Date helpers  (LocalDate <-> String)
    // -----------------------------------------------------------------------

    /**
     * Converts a {@link LocalDate} to its ISO string representation.
     * Returns an empty string for {@code null} to match the original behaviour.
     */
    @Named("routineDateToString")
    public String routineDateToString(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    /**
     * Parses a date string in {@code MM/dd/yyyy} format.
     * Returns {@link LocalDate#now()} when the input is blank.
     * Used for the "add routine with drills" path.
     */
    @Named("routineDateFromSlashFormat")
    public LocalDate routineDateFromSlashFormat(String date) {
        return StringUtils.isNotEmpty(date)
                ? FitmateUtil.getLocalDateFromString("MM/dd/yyyy", date)
                : LocalDate.now();
    }

    /**
     * Parses a date string in {@code yyyy-MM-dd} format.
     * Returns {@link LocalDate#now()} when the input is blank.
     * Used for the "add routine without drills" / generator path.
     */
    @Named("routineDateFromDashFormat")
    public LocalDate routineDateFromDashFormat(String date) {
        return StringUtils.isNotEmpty(date)
                ? FitmateUtil.getLocalDateFromString("yyyy-MM-dd", date)
                : LocalDate.now();
    }
}
