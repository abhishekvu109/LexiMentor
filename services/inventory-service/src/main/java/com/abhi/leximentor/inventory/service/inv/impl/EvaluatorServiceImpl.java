package com.abhi.leximentor.inventory.service.inv.impl;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.inv.EvaluatorDTO;
import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import com.abhi.leximentor.inventory.repository.inv.EvaluatorRepository;
import com.abhi.leximentor.inventory.service.inv.EvaluatorService;
import com.abhi.leximentor.inventory.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EvaluatorServiceImpl implements EvaluatorService {

    private final EvaluatorRepository evaluatorRepository;

    @Override
    public EvaluatorDTO add(EvaluatorDTO dto) {
        log.info("Adding evaluator. name={}, drillType={}", dto == null ? null : dto.getName(), dto == null ? null : dto.getDrillType());
        Evaluator evaluator = Evaluator.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).status(Status.ApplicationStatus.ACTIVE).drillType(DrillTypes.getType(dto.getDrillType()).name()).name(dto.getName()).build();
        evaluator = evaluatorRepository.save(evaluator);
        EvaluatorDTO response = EvaluatorDTO.builder().refId(evaluator.getRefId()).crtnDate(evaluator.getCrtnDate()).name(evaluator.getName()).status(Status.ApplicationStatus.getStatusStr(evaluator.getStatus())).drillType(evaluator.getDrillType()).build();
        log.info("Added evaluator. refId={}", response.getRefId());
        return response;
    }

    @Override
    public List<EvaluatorDTO> addAll(List<EvaluatorDTO> list) {
        log.info("Adding evaluators. count={}", list == null ? 0 : list.size());
        List<EvaluatorDTO> response = list.stream().map(this::add).collect(Collectors.toList());
        log.info("Added evaluators. count={}", response.size());
        return response;
    }

    @Override
    public EvaluatorDTO getByName(String name) {
        log.info("Fetching evaluator by name={}", name);
        Evaluator evaluator = evaluatorRepository.findByName(name);
        EvaluatorDTO response = EvaluatorDTO.builder().refId(evaluator.getRefId()).crtnDate(evaluator.getCrtnDate()).name(evaluator.getName()).status(Status.ApplicationStatus.getStatusStr(evaluator.getStatus())).drillType(evaluator.getDrillType()).build();
        log.info("Fetched evaluator by name={}", name);
        return response;

    }

    @Override
    public EvaluatorDTO getByRefId(long refId) {
        log.info("Fetching evaluator by refId={}", refId);
        Evaluator evaluator = evaluatorRepository.findByRefId(refId);
        EvaluatorDTO response = EvaluatorDTO.builder().refId(evaluator.getRefId()).crtnDate(evaluator.getCrtnDate()).name(evaluator.getName()).status(Status.ApplicationStatus.getStatusStr(evaluator.getStatus())).drillType(evaluator.getDrillType()).build();
        log.info("Fetched evaluator by refId={}", refId);
        return response;
    }

    @Override
    public List<EvaluatorDTO> getByDrillType(String drillType) {
        log.info("Fetching evaluators by drillType={}", drillType);
        List<Evaluator> evaluators = evaluatorRepository.findByDrillType(drillType);
        List<EvaluatorDTO> response = evaluators.stream().map(evaluator -> EvaluatorDTO.builder().refId(evaluator.getRefId()).crtnDate(evaluator.getCrtnDate()).name(evaluator.getName()).status(Status.ApplicationStatus.getStatusStr(evaluator.getStatus())).drillType(evaluator.getDrillType()).build()).collect(Collectors.toList());
        log.info("Fetched evaluators by drillType. count={}", response.size());
        return response;
    }
}
