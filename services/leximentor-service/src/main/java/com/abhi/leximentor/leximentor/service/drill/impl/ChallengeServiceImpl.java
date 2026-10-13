package com.abhi.leximentor.leximentor.service.drill.impl;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.constants.EvaluationStatus;
import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeDTO;
import com.abhi.leximentor.leximentor.dto.drill.DrillDTO;
import com.abhi.leximentor.leximentor.dto.drill.filters.ChallengeSearchFilter;
import com.abhi.leximentor.leximentor.dto.inv.EvaluatorDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeEvaluation;
import com.abhi.leximentor.leximentor.entities.drill.Drill;
import com.abhi.leximentor.leximentor.exceptions.entities.InvalidDTOException;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeEvaluationRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillRepository;
import com.abhi.leximentor.leximentor.service.base.AbstractApplicationService;
import com.abhi.leximentor.leximentor.service.drill.ChallengeService;
import com.abhi.leximentor.leximentor.service.inv.EvaluatorService;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChallengeServiceImpl extends AbstractApplicationService implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final DrillRepository drillRepository;
    private final ChallengeEvaluationRepository challengeEvaluationRepository;
    private final EvaluatorService evaluatorService;
    private final DrillDomainMapper drillDomainMapper;

    @Override
    @Transactional
    public DrillDTO addChallenges(DrillDTO drillDTO, ChallengeType challengeType, String username) {
        if (drillDTO == null) {
            throw new InvalidDTOException("The drill dto is null.");
        }
        log.info("Adding challenge. drillRefId={}, drillType={}", drillDTO.getKey(), challengeType);
        Drill drill = requireEntity(drillRepository.findByKey(drillDTO.getKey()).orElse(null), "Drill metadata not found for key: " + drillDTO.getKey());
        List<Challenge> challenges = drill.getChallenges();
        if (CollectionUtil.isEmpty(drill.getChallenges())) challenges = new LinkedList<>();
        challenges.add(drillDomainMapper.toEntity(drill, challengeType, username));
        drill.setChallenges(challenges);
        drill = drillRepository.save(drill);
        DrillDTO response = drillDomainMapper.toDto(drill);
        log.info("Added challenge. drillRefId={}, totalChallenges={}", response.getKey(), response.getChallengeDTOList() == null ? 0 : response.getChallengeDTOList().size());
        return response;
    }

    @Override
    public List<ChallengeDTO> getChallengesByDrillKey(String drillKey) {
        log.info("Fetching challenges by drill Key={}", drillKey);
        Drill drill = requireEntity(drillRepository.findByKey(drillKey).orElse(null), "Drill metadata not found for key: " + drillKey);
        List<ChallengeDTO> response = CollectionUtil.isNotEmpty(drill.getChallenges()) ? drill.getChallenges().stream().map(drillDomainMapper::toDto).collect(Collectors.toList()) : new LinkedList<>();
        log.info("Fetched challenges by drillKey={}, count={}", drillKey, response.size());
        return response;
    }

    @Override
    public List<ChallengeDTO> getChallengesByDrillKeyAndUsername(String drillKey, String username) {
        log.info("Fetching challenges by drillRefId={}", drillKey);
        Drill drill = requireEntity(drillRepository.findByKey(drillKey).orElse(null), "Drill metadata not found for refId: " + drillKey);
        List<Challenge> challenges = challengeRepository.findByDrillAndUsernameIgnoreCase(drill, username);
        List<ChallengeDTO> response = CollectionUtil.isNotEmpty(challenges) ? challenges.stream().map(drillDomainMapper::toDto).toList() : new LinkedList<>();
        log.info("Fetched challenges by drillRefId={}, count={}", drillKey, response.size());
        return response;
    }

    @Override
    public void deleteChallenge(String drillKey) {
        log.info("Deleting challenge. drillRefId={}", drillKey);
        Challenge challenge = requireEntity(challengeRepository.findByKey(drillKey), "Drill challenge not found for refId: " + drillKey);
        List<ChallengeEvaluation> challengeEvaluations = challengeEvaluationRepository.findByChallengeScoresIn(challenge.getChallengeScoresList());
        challengeEvaluationRepository.deleteAll(challengeEvaluations);
        challengeRepository.delete(challenge);
        log.info("Deleted challenge. drillRefId={}", drillKey);
    }

    @Override
    public List<EvaluatorDTO> getEvaluatorsByChallengeKey(String challengeKey) {
        log.info("Fetching evaluators by challengeRefId={}", challengeKey);
        Challenge challenge = requireEntity(challengeRepository.findByKey(challengeKey), "Drill challenge not found for refId: " + challengeKey);
        List<EvaluatorDTO> response = evaluatorService.getByDrillType(challenge.getChallengeType());
        log.info("Fetched evaluators by challengeRefId={}, count={}", challengeKey, response.size());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<ChallengeDTO>> search(ChallengeSearchFilter filter) {
        if (filter == null) {
            throw new InvalidDTOException("Search filter cannot be null.");
        }
        if (StringUtils.isBlank(filter.getUsername())) {
            log.error("username is mandatory.");
            return Optional.empty();
        }
        String username = filter.getUsername().trim().toLowerCase();
        Specification<Challenge> spec = Specification.where(null);
        spec = spec.and(((root, query, cb) -> cb.equal(cb.lower(root.get("username")), username)));
        spec = StringUtils.isNotEmpty(filter.getKey()) ? spec.and(((root, query, cb) -> cb.equal(root.get("key"), filter.getKey()))) : spec;
        if (StringUtils.isNotBlank(filter.getStatus())) {
            if (!StringUtils.equalsAnyIgnoreCase(filter.getStatus(), Status.ApplicationStatus.ACTIVE_STR, Status.ApplicationStatus.INACTIVE_STR)) {
                throw new InvalidDTOException("status must be either ACTIVE or INACTIVE.");
            }
            spec = spec.and(((root, query, cb) -> cb.equal(root.get("status"), Status.ApplicationStatus.getStatus(filter.getStatus()))));
        }
        if (StringUtils.isNotBlank(filter.getEvaluationStatus())) {
            int evaluationStatus = EvaluationStatus.of(filter.getEvaluationStatus());
            spec = spec.and(((root, query, cb) -> cb.equal(root.get("evaluationStatus"), evaluationStatus)));
        }
        if (StringUtils.isNotBlank(filter.getDrillKey())) {
            String drillKey = filter.getDrillKey();
            spec = spec.and(((root, query, cb) -> cb.equal(root.join("drillKey").get("key"), drillKey)));
        }
        if (StringUtils.isNotBlank(filter.getChallengeType())) {
            ChallengeType challengeType = ChallengeType.of(filter.getChallengeType().trim().toLowerCase());
            spec = spec.and(((root, query, cb) -> cb.equal(cb.lower(root.get("challengeType")), challengeType)));
        }
        if (filter.getScoreFrom() != null && filter.getScoreTo() != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("score"), filter.getScoreFrom(), filter.getScoreTo()));
        } else if (filter.getScoreFrom() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("score"), filter.getScoreFrom()));
        } else if (filter.getScoreTo() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("score"), filter.getScoreTo()));
        }
        String sortBy = StringUtils.defaultIfBlank(filter.getSortBy(), "createdAt");
        Sort.Direction sortDirection;
        try {
            sortDirection = StringUtils.isBlank(filter.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.fromString(filter.getSortDir());
        } catch (IllegalArgumentException ex) {
            throw new InvalidDTOException("sortDir must be either 'asc' or 'desc'.");
        }
        Sort sort = Sort.by(sortDirection, sortBy);
        List<Challenge> challenges = challengeRepository.findAll(spec, sort);
        return Optional.of(challenges.stream().map(drillDomainMapper::toDto).toList());
    }


}
