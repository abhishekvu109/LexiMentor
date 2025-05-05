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
        Evaluator evaluator = Evaluator.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).status(Status.ApplicationStatus.ACTIVE).drillType(DrillTypes.getType(dto.getDrillType()).name()).name(dto.getName()).build();
        evaluator = evaluatorRepository.save(evaluator);
        return EvaluatorDTO.builder().refId(evaluator.getRefId()).crtnDate(evaluator.getCrtnDate()).name(evaluator.getName()).status(Status.ApplicationStatus.getStatusStr(evaluator.getStatus())).drillType(evaluator.getDrillType()).build();
    }

    @Override
    public List<EvaluatorDTO> addAll(List<EvaluatorDTO> list) {
        return list.stream().map(this::add).collect(Collectors.toList());
    }

    @Override
    public EvaluatorDTO getByName(String name) {
        Evaluator evaluator = evaluatorRepository.findByName(name);
        return EvaluatorDTO.builder().refId(evaluator.getRefId()).crtnDate(evaluator.getCrtnDate()).name(evaluator.getName()).status(Status.ApplicationStatus.getStatusStr(evaluator.getStatus())).drillType(evaluator.getDrillType()).build();

    }

    @Override
    public EvaluatorDTO getByRefId(long refId) {
        Evaluator evaluator = evaluatorRepository.findByRefId(refId);
        return EvaluatorDTO.builder().refId(evaluator.getRefId()).crtnDate(evaluator.getCrtnDate()).name(evaluator.getName()).status(Status.ApplicationStatus.getStatusStr(evaluator.getStatus())).drillType(evaluator.getDrillType()).build();
    }

    @Override
    public List<EvaluatorDTO> getByDrillType(String drillType) {
        List<Evaluator> evaluators = evaluatorRepository.findByDrillType(drillType);
        return evaluators.stream().map(evaluator -> EvaluatorDTO.builder().refId(evaluator.getRefId()).crtnDate(evaluator.getCrtnDate()).name(evaluator.getName()).status(Status.ApplicationStatus.getStatusStr(evaluator.getStatus())).drillType(evaluator.getDrillType()).build()).collect(Collectors.toList());
    }
}
