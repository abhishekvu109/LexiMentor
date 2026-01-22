package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.constants.Status;
import com.abhi.saarthi.cashflow.dto.EarningDTO;
import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.entities.Earning;
import com.abhi.saarthi.cashflow.exceptions.entities.ServerException;
import com.abhi.saarthi.cashflow.mappers.EarningMapper;
import com.abhi.saarthi.cashflow.model.EarningSearchFilter;
import com.abhi.saarthi.cashflow.repository.EarningRepository;
import com.abhi.saarthi.cashflow.service.EarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EarningServiceImpl implements EarningService {

    private final EarningRepository earningRepository;
    private final EarningMapper earningMapper;

    @Override
    @Transactional
    public List<EarningDTO> add(List<EarningDTO> dtoList) {
        List<Earning> earnings = earningRepository.saveAll(dtoList.stream().map(earningMapper::toEntity).toList());
        return earnings.stream().map(earningMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public List<EarningDTO> update(List<EarningDTO> dtoList) {
        List<Earning> earnings = earningRepository.saveAll(dtoList.stream().map(dto -> {
            Earning earning = earningRepository.findByRefId(Long.parseLong(dto.getRefId()))
                    .orElseThrow(() ->
                            new ServerException.EntityObjectNotFound(String.format("Entity object earning is not found to update:%s", dto.getRefId())));
            earning.setAmount(dto.getAmount());
            earning.setNotes(dto.getNotes());
            earning.setSource(dto.getSource());
            earning.setDepositDate(dto.getDepositDate());
            return earning;
        }).toList());
        return earnings.stream().map(earningMapper::toDTO).toList();
    }

    @Override
    public void delete(List<EarningDTO> dtoList) {
        List<Earning> earnings = dtoList.stream().map(dto -> earningRepository.findByRefId(Long.parseLong(dto.getRefId())).orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object not found for %s", dto.getRefId())))).toList();
        earningRepository.deleteAll(earnings);
    }

    @Override
    public List<EarningDTO> search(EarningSearchFilter filter) {
        if (filter == null || filter.isEmpty()) {
            EarningSearchFilter defaultFilter = EarningSearchFilter.defaultFilter();
            Sort sort = Sort.by(Sort.Direction.fromString(defaultFilter.getSortDir()), defaultFilter.getSortBy());
            return earningRepository.findAll(sort).stream()
                    .map(earningMapper::toDTO).toList();
        }
        Specification<Earning> spec = Specification.unrestricted();
        spec = StringUtils.isNotEmpty(filter.getRefId()) ? spec.and(((root, query, cb) -> cb.equal(root.get("refId"), Long.parseLong(filter.getRefId())))) : spec;
        spec = StringUtils.isNotEmpty(filter.getUuid()) ? spec.and(((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getStatus()) ? spec.and(((root, query, cb) -> cb.equal(root.get("status"), Status.ApplicationStatus.getStatus(filter.getStatus())))) : spec;
        spec = StringUtils.isNotEmpty(filter.getUsername()) ? spec.and(((root, query, cb) -> cb.equal(root.get("username"), filter.getUsername()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getSource()) ? spec.and(((root, query, cb) -> cb.equal(root.get("source"), filter.getSource()))) : spec;
        spec = filter.getAmountFrom() != null ? spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), filter.getAmountFrom())) : spec;
        spec = filter.getAmountTo() != null ? spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), filter.getAmountTo())) : spec;
        if (!StringUtils.isAnyEmpty(filter.getSortDir(), filter.getSortBy())) {
            Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
            List<Earning> earnings = earningRepository.findAll(spec, sort);
            return earnings.stream().map(earningMapper::toDTO).toList();
        }
        List<Earning> earnings = earningRepository.findAll(spec);
        return earnings.stream().map(earningMapper::toDTO).toList();
    }

    @Override
    public EarningDTO findByRefId(long refId) {
        return earningMapper.toDTO(earningRepository.findByRefId(refId).orElseThrow(() -> new ServerException.EntityObjectNotFound("Entity object is not found.")));
    }
}
