package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.*;
import com.abhi.leximentor.fitmate.entities.FoodEntry;
import com.abhi.leximentor.fitmate.entities.NutritionGoal;
import com.abhi.leximentor.fitmate.repository.FoodEntryRepository;
import com.abhi.leximentor.fitmate.repository.NutritionGoalRepository;
import com.abhi.leximentor.fitmate.service.NutritionService;
import com.abhi.leximentor.fitmate.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NutritionServiceImpl implements NutritionService {
    private static final Set<String> ALLOWED_MEAL_TYPES = Set.of("BREAKFAST", "LUNCH", "DINNER", "SNACK", "OTHER");
    private static final Set<String> ALLOWED_SOURCE_TYPES = Set.of("MANUAL", "LIBRARY", "BARCODE");
    private static final double ADHERENCE_LOWER_FACTOR = 0.9;
    private static final double ADHERENCE_UPPER_FACTOR = 1.1;

    private final NutritionGoalRepository nutritionGoalRepository;
    private final FoodEntryRepository foodEntryRepository;

    @Override
    @Transactional
    public NutritionGoalDTO upsertGoal(NutritionGoalDTO goalDTO) {
        validateGoal(goalDTO);
        NutritionGoal activeGoal = nutritionGoalRepository.findTopByUsernameAndEffectiveToIsNullOrderByEffectiveFromDesc(goalDTO.getUsername());
        if (activeGoal == null) {
            activeGoal = NutritionGoal.builder()
                    .uuid(KeyGeneratorUtil.uuid())
                    .refId(KeyGeneratorUtil.refId())
                    .username(goalDTO.getUsername().trim())
                    .build();
        }

        activeGoal.setDailyCaloriesTarget(goalDTO.getDailyCaloriesTarget());
        activeGoal.setProteinTarget(goalDTO.getProteinTarget());
        activeGoal.setCarbTarget(goalDTO.getCarbTarget());
        activeGoal.setFatTarget(goalDTO.getFatTarget());
        activeGoal.setEffectiveFrom(goalDTO.getEffectiveFrom() == null ? LocalDate.now() : goalDTO.getEffectiveFrom());
        activeGoal.setEffectiveTo(goalDTO.getEffectiveTo());

        return toGoalDTO(nutritionGoalRepository.save(activeGoal));
    }

    @Override
    public NutritionGoalDTO getActiveGoal(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username is required");
        }
        NutritionGoal goal = nutritionGoalRepository.findTopByUsernameAndEffectiveToIsNullOrderByEffectiveFromDesc(username.trim());
        return goal == null ? null : toGoalDTO(goal);
    }

    @Override
    @Transactional
    public FoodEntryDTO addEntry(FoodEntryDTO foodEntryDTO) {
        validateEntry(foodEntryDTO, true);
        FoodEntry entry = FoodEntry.builder()
                .uuid(KeyGeneratorUtil.uuid())
                .refId(KeyGeneratorUtil.refId())
                .username(foodEntryDTO.getUsername().trim())
                .entryDate(foodEntryDTO.getEntryDate())
                .entryTime(foodEntryDTO.getEntryTime() == null ? LocalTime.now() : foodEntryDTO.getEntryTime())
                .mealType(normalizeMealType(foodEntryDTO.getMealType()))
                .foodName(foodEntryDTO.getFoodName().trim())
                .servingQty(foodEntryDTO.getServingQty())
                .servingUnit(foodEntryDTO.getServingUnit())
                .calories(foodEntryDTO.getCalories())
                .protein(foodEntryDTO.getProtein())
                .carbs(foodEntryDTO.getCarbs())
                .fat(foodEntryDTO.getFat())
                .fiber(foodEntryDTO.getFiber())
                .sugar(foodEntryDTO.getSugar())
                .sodium(foodEntryDTO.getSodium())
                .sourceType(normalizeSourceType(foodEntryDTO.getSourceType()))
                .notes(foodEntryDTO.getNotes())
                .build();
        return toFoodEntryDTO(foodEntryRepository.save(entry));
    }

    @Override
    @Transactional
    public FoodEntryDTO updateEntry(String username, long refId, FoodEntryDTO foodEntryDTO) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username is required");
        }
        validateEntry(foodEntryDTO, false);
        FoodEntry entry = foodEntryRepository.findByRefIdAndUsername(refId, username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Food entry not found"));
        entry.setEntryDate(foodEntryDTO.getEntryDate());
        entry.setEntryTime(foodEntryDTO.getEntryTime() == null ? entry.getEntryTime() : foodEntryDTO.getEntryTime());
        entry.setMealType(normalizeMealType(foodEntryDTO.getMealType()));
        entry.setFoodName(foodEntryDTO.getFoodName().trim());
        entry.setServingQty(foodEntryDTO.getServingQty());
        entry.setServingUnit(foodEntryDTO.getServingUnit());
        entry.setCalories(foodEntryDTO.getCalories());
        entry.setProtein(foodEntryDTO.getProtein());
        entry.setCarbs(foodEntryDTO.getCarbs());
        entry.setFat(foodEntryDTO.getFat());
        entry.setFiber(foodEntryDTO.getFiber());
        entry.setSugar(foodEntryDTO.getSugar());
        entry.setSodium(foodEntryDTO.getSodium());
        entry.setSourceType(normalizeSourceType(foodEntryDTO.getSourceType()));
        entry.setNotes(foodEntryDTO.getNotes());
        return toFoodEntryDTO(foodEntryRepository.save(entry));
    }

    @Override
    @Transactional
    public void deleteEntry(String username, long refId) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username is required");
        }
        FoodEntry entry = foodEntryRepository.findByRefIdAndUsername(refId, username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Food entry not found"));
        foodEntryRepository.delete(entry);
    }

    @Override
    public List<FoodEntryDTO> getEntries(String username, LocalDate fromDate, LocalDate toDate, String mealType) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username is required");
        }
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        List<FoodEntry> entries;
        if (StringUtils.isBlank(mealType)) {
            entries = foodEntryRepository.findByUsernameAndEntryDateBetweenOrderByEntryDateAscEntryTimeAsc(username.trim(), fromDate, toDate);
        } else {
            String normalizedMealType = normalizeMealType(mealType);
            entries = new ArrayList<>();
            LocalDate current = fromDate;
            while (!current.isAfter(toDate)) {
                entries.addAll(foodEntryRepository.findByUsernameAndEntryDateAndMealTypeOrderByEntryTimeAsc(username.trim(), current, normalizedMealType));
                current = current.plusDays(1);
            }
            entries.sort(Comparator.comparing(FoodEntry::getEntryDate).thenComparing(FoodEntry::getEntryTime, Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return entries.stream().map(this::toFoodEntryDTO).toList();
    }

    @Override
    public NutritionDailySummaryDTO getDailySummary(String username, LocalDate date) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username is required");
        }
        LocalDate summaryDate = date == null ? LocalDate.now() : date;
        List<FoodEntry> entries = foodEntryRepository.findByUsernameAndEntryDateOrderByEntryTimeAsc(username.trim(), summaryDate);
        NutritionGoal goal = nutritionGoalRepository.findTopByUsernameAndEffectiveToIsNullOrderByEffectiveFromDesc(username.trim());

        double consumedCalories = entries.stream().mapToDouble(FoodEntry::getCalories).sum();
        double consumedProtein = entries.stream().mapToDouble(FoodEntry::getProtein).sum();
        double consumedCarbs = entries.stream().mapToDouble(FoodEntry::getCarbs).sum();
        double consumedFat = entries.stream().mapToDouble(FoodEntry::getFat).sum();
        double target = goal == null ? 0d : goal.getDailyCaloriesTarget();

        List<NutritionMealSummaryDTO> mealSummaries = entries.stream()
                .collect(Collectors.groupingBy(FoodEntry::getMealType))
                .entrySet().stream()
                .map(entry -> NutritionMealSummaryDTO.builder()
                        .mealType(entry.getKey())
                        .calories(entry.getValue().stream().mapToDouble(FoodEntry::getCalories).sum())
                        .protein(entry.getValue().stream().mapToDouble(FoodEntry::getProtein).sum())
                        .carbs(entry.getValue().stream().mapToDouble(FoodEntry::getCarbs).sum())
                        .fat(entry.getValue().stream().mapToDouble(FoodEntry::getFat).sum())
                        .build())
                .sorted(Comparator.comparing(NutritionMealSummaryDTO::getMealType))
                .toList();

        return NutritionDailySummaryDTO.builder()
                .username(username.trim())
                .date(summaryDate)
                .caloriesTarget(target)
                .consumedCalories(consumedCalories)
                .remainingCalories(target - consumedCalories)
                .consumedProtein(consumedProtein)
                .consumedCarbs(consumedCarbs)
                .consumedFat(consumedFat)
                .totalEntries(entries.size())
                .mealSummaries(mealSummaries)
                .build();
    }

    @Override
    public NutritionTrendSummaryDTO getTrends(String username, LocalDate fromDate, LocalDate toDate) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username is required");
        }
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        List<FoodEntry> entries = foodEntryRepository.findByUsernameAndEntryDateBetweenOrderByEntryDateAscEntryTimeAsc(username.trim(), fromDate, toDate);
        Map<LocalDate, List<FoodEntry>> byDate = entries.stream().collect(Collectors.groupingBy(FoodEntry::getEntryDate, TreeMap::new, Collectors.toList()));
        NutritionGoal goal = nutritionGoalRepository.findTopByUsernameAndEffectiveToIsNullOrderByEffectiveFromDesc(username.trim());
        double target = goal == null ? 0d : goal.getDailyCaloriesTarget();

        List<NutritionTrendPointDTO> points = byDate.entrySet().stream()
                .map(entry -> {
                    double consumedCalories = entry.getValue().stream().mapToDouble(FoodEntry::getCalories).sum();
                    boolean withinRange = target > 0
                            && consumedCalories >= target * ADHERENCE_LOWER_FACTOR
                            && consumedCalories <= target * ADHERENCE_UPPER_FACTOR;
                    return NutritionTrendPointDTO.builder()
                            .date(entry.getKey())
                            .consumedCalories(consumedCalories)
                            .consumedProtein(entry.getValue().stream().mapToDouble(FoodEntry::getProtein).sum())
                            .consumedCarbs(entry.getValue().stream().mapToDouble(FoodEntry::getCarbs).sum())
                            .consumedFat(entry.getValue().stream().mapToDouble(FoodEntry::getFat).sum())
                            .totalEntries(entry.getValue().size())
                            .withinGoalRange(withinRange)
                            .build();
                })
                .toList();

        int adherentDays = (int) points.stream().filter(NutritionTrendPointDTO::isWithinGoalRange).count();
        double adherencePercentage = points.isEmpty() ? 0d : (adherentDays * 100.0) / points.size();

        return NutritionTrendSummaryDTO.builder()
                .username(username.trim())
                .fromDate(fromDate)
                .toDate(toDate)
                .caloriesTarget(target)
                .daysWithEntries(points.size())
                .adherentDays(adherentDays)
                .adherencePercentage(adherencePercentage)
                .points(points)
                .build();
    }

    private void validateGoal(NutritionGoalDTO goalDTO) {
        if (goalDTO == null) {
            throw new IllegalArgumentException("goal payload is required");
        }
        if (StringUtils.isBlank(goalDTO.getUsername())) {
            throw new IllegalArgumentException("username is required");
        }
        if (goalDTO.getDailyCaloriesTarget() <= 0) {
            throw new IllegalArgumentException("dailyCaloriesTarget must be greater than 0");
        }
        if (goalDTO.getProteinTarget() < 0 || goalDTO.getCarbTarget() < 0 || goalDTO.getFatTarget() < 0) {
            throw new IllegalArgumentException("Macro targets cannot be negative");
        }
    }

    private void validateEntry(FoodEntryDTO dto, boolean requireUsername) {
        if (dto == null) {
            throw new IllegalArgumentException("entry payload is required");
        }
        if (requireUsername && StringUtils.isBlank(dto.getUsername())) {
            throw new IllegalArgumentException("username is required");
        }
        if (dto.getEntryDate() == null) {
            throw new IllegalArgumentException("entryDate is required");
        }
        if (StringUtils.isBlank(dto.getMealType())) {
            throw new IllegalArgumentException("mealType is required");
        }
        if (StringUtils.isBlank(dto.getFoodName())) {
            throw new IllegalArgumentException("foodName is required");
        }
        if (dto.getCalories() < 0 || dto.getProtein() < 0 || dto.getCarbs() < 0 || dto.getFat() < 0
                || dto.getFiber() < 0 || dto.getSugar() < 0 || dto.getSodium() < 0 || dto.getServingQty() < 0) {
            throw new IllegalArgumentException("Numeric values cannot be negative");
        }
        if (dto.getCalories() > 10000) {
            throw new IllegalArgumentException("calories is too high");
        }
        normalizeMealType(dto.getMealType());
        normalizeSourceType(dto.getSourceType());
    }

    private String normalizeMealType(String mealType) {
        String normalized = mealType.trim().toUpperCase(Locale.ENGLISH);
        if (!ALLOWED_MEAL_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported mealType");
        }
        return normalized;
    }

    private String normalizeSourceType(String sourceType) {
        String normalized = StringUtils.isBlank(sourceType) ? "MANUAL" : sourceType.trim().toUpperCase(Locale.ENGLISH);
        if (!ALLOWED_SOURCE_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported sourceType");
        }
        return normalized;
    }

    private NutritionGoalDTO toGoalDTO(NutritionGoal entity) {
        return NutritionGoalDTO.builder()
                .refId(String.valueOf(entity.getRefId()))
                .username(entity.getUsername())
                .dailyCaloriesTarget(entity.getDailyCaloriesTarget())
                .proteinTarget(entity.getProteinTarget())
                .carbTarget(entity.getCarbTarget())
                .fatTarget(entity.getFatTarget())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTo(entity.getEffectiveTo())
                .crtnDate(entity.getCrtnDate())
                .lastUpdDate(entity.getLastUpdDate())
                .build();
    }

    private FoodEntryDTO toFoodEntryDTO(FoodEntry entity) {
        return FoodEntryDTO.builder()
                .refId(String.valueOf(entity.getRefId()))
                .username(entity.getUsername())
                .entryDate(entity.getEntryDate())
                .entryTime(entity.getEntryTime())
                .mealType(entity.getMealType())
                .foodName(entity.getFoodName())
                .servingQty(entity.getServingQty())
                .servingUnit(entity.getServingUnit())
                .calories(entity.getCalories())
                .protein(entity.getProtein())
                .carbs(entity.getCarbs())
                .fat(entity.getFat())
                .fiber(entity.getFiber())
                .sugar(entity.getSugar())
                .sodium(entity.getSodium())
                .sourceType(entity.getSourceType())
                .notes(entity.getNotes())
                .crtnDate(entity.getCrtnDate())
                .lastUpdDate(entity.getLastUpdDate())
                .build();
    }
}
