package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.constants.Status;
import com.abhi.saarthi.cashflow.dto.DepositDTO;
import com.abhi.saarthi.cashflow.entities.Deposit;
import com.abhi.saarthi.cashflow.exceptions.entities.ServerException;
import com.abhi.saarthi.cashflow.mappers.DepositMapper;
import com.abhi.saarthi.cashflow.model.DepositSearchFilter;
import com.abhi.saarthi.cashflow.repository.DepositRepository;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import com.abhi.saarthi.cashflow.service.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;
    private final HouseholdRepository householdRepository;

    @Override
    @Transactional
    public List<DepositDTO> add(List<DepositDTO> dtoList) {
        List<Deposit> deposits = depositRepository.saveAll(dtoList.stream().map(dto -> {
            Deposit deposit = depositMapper.toEntity(dto);
            deposit.setHousehold(householdRepository.findByRefId(Long.parseLong(dto.getHouseholdRefId()))
                    .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getHouseholdRefId()))));
            return deposit;
        }).toList());
        return deposits.stream().map(depositMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public List<DepositDTO> update(List<DepositDTO> dtoList) {
        List<Deposit> deposits = depositRepository.saveAll(dtoList.stream().map(dto -> {
            Deposit deposit = depositRepository.findByRefId(Long.parseLong(dto.getRefId()))
                    .orElseThrow(() ->
                            new ServerException.EntityObjectNotFound(String.format("Entity object earning is not found to update:%s", dto.getRefId())));
            deposit.setAmount(dto.getAmount());
            deposit.setNotes(dto.getNotes());
            deposit.setSource(dto.getSource());
            deposit.setDepositDate(dto.getDepositDate());
            return deposit;
        }).toList());
        return deposits.stream().map(depositMapper::toDTO).toList();
    }

    @Override
    public void delete(List<DepositDTO> dtoList) {
        List<Deposit> deposits = dtoList.stream().map(dto -> depositRepository.findByRefId(Long.parseLong(dto.getRefId())).orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object not found for %s", dto.getRefId())))).toList();
        depositRepository.deleteAll(deposits);
    }

    @Override
    public List<DepositDTO> search(DepositSearchFilter filter) {
        if (filter == null || filter.isEmpty()) {
            DepositSearchFilter defaultFilter = DepositSearchFilter.defaultFilter();
            Sort sort = Sort.by(Sort.Direction.fromString(defaultFilter.getSortDir()), defaultFilter.getSortBy());
            return depositRepository.findAll(sort).stream()
                    .map(depositMapper::toDTO).toList();
        }
        Specification<Deposit> spec = Specification.unrestricted();
        spec = StringUtils.isNotEmpty(filter.getRefId()) ? spec.and(((root, query, cb) -> cb.equal(root.get("refId"), Long.parseLong(filter.getRefId())))) : spec;
        spec = StringUtils.isNotEmpty(filter.getUuid()) ? spec.and(((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getStatus()) ? spec.and(((root, query, cb) -> cb.equal(root.get("status"), Status.ApplicationStatus.getStatus(filter.getStatus())))) : spec;
        spec = StringUtils.isNotEmpty(filter.getUsername()) ? spec.and(((root, query, cb) -> cb.equal(root.get("username"), filter.getUsername()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getSource()) ? spec.and(((root, query, cb) -> cb.equal(root.get("source"), filter.getSource()))) : spec;
        spec = filter.getAmountFrom() != null ? spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), filter.getAmountFrom())) : spec;
        spec = filter.getAmountTo() != null ? spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), filter.getAmountTo())) : spec;
        if (!StringUtils.isAnyEmpty(filter.getSortDir(), filter.getSortBy())) {
            Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
            List<Deposit> deposits = depositRepository.findAll(spec, sort);
            return deposits.stream().map(depositMapper::toDTO).toList();
        }
        List<Deposit> deposits = depositRepository.findAll(spec);
        return deposits.stream().map(depositMapper::toDTO).toList();
    }

    @Override
    public DepositDTO findByRefId(long refId) {
        return depositMapper.toDTO(depositRepository.findByRefId(refId).orElseThrow(() -> new ServerException.EntityObjectNotFound("Entity object is not found.")));

    }
}
