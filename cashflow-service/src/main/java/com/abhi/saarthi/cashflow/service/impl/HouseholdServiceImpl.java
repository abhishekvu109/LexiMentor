package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.constants.Currency;
import com.abhi.saarthi.cashflow.constants.Status;
import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.entities.Household;
import com.abhi.saarthi.cashflow.exceptions.entities.ServerException;
import com.abhi.saarthi.cashflow.model.HouseholdSearchFilter;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import com.abhi.saarthi.cashflow.service.HouseholdService;
import com.abhi.saarthi.cashflow.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HouseholdServiceImpl implements HouseholdService {
    private final HouseholdRepository householdRepository;

    private static Specification<Household> withRefId(String refId) {
        if (StringUtils.isNotEmpty(refId)) {
            return (root, query, cb) -> cb.equal(root.get("refId"), refId);
        }
        return null;
    }

    private static Specification<Household> withName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            return (root, query, cb) -> cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%s");
        }
        return null;
    }

    private static Specification<Household> withCurrency(String currency) {
        if (StringUtils.isNotEmpty(currency)) {
            return (root, query, cb) -> cb.equal(root.get("currency"), Currency.parse(currency));
        }
        return null;
    }

    private static Specification<Household> withStatus(String status) {
        if (StringUtils.isNotEmpty(status)) {
            int statusInt = Status.ApplicationStatus.getStatus(status);
            return (root, query, cb) -> cb.equal(root.get("status"), statusInt);
        }
        return null;
    }

    private static Specification<Household> withUUID(String uuid) {
        if (StringUtils.isNotEmpty(uuid)) {
            return (root, query, cb) -> cb.equal(root.get("uuid"), uuid);
        }
        return null;
    }

    @Override
    @Transactional
    public List<HouseholdDTO> add(List<HouseholdDTO> householdDTOS) {
        log.info("Adding new households: {}", householdDTOS);
        List<Household> households = householdDTOS.stream().map(ServiceUtil.HouseholdUtil::buildEntity).toList();
        households = householdRepository.saveAll(households);
        log.info("Successfully added new households: {}", households);
        return households.stream().map(ServiceUtil.HouseholdUtil::buildDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<HouseholdDTO> update(List<HouseholdDTO> householdDTOS) {
        log.info("Updating households: {}", householdDTOS);
        List<Household> households = householdDTOS.stream().map(dto -> {
            Household household = householdRepository
                    .findByRefId(Long.parseLong(dto.getRefId()))
                    .orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getRefId())));
            return ServiceUtil.HouseholdUtil.updateEntity(household, dto);
        }).toList();
        households = householdRepository.saveAll(households);
        log.info("Successfully updated households: {}", households);
        return households.stream().map(ServiceUtil.HouseholdUtil::buildDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(List<HouseholdDTO> householdDTOS) {
        log.info("Deleting households: {}", householdDTOS);
        List<Household> households = householdDTOS.stream().map(dto -> householdRepository
                .findByRefId(Long.parseLong(dto.getRefId()))
                .orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getRefId())))).toList();
        householdRepository.deleteAll(households);
        log.info("Successfully deleted households");
    }

    @Override
    public List<HouseholdDTO> search(HouseholdSearchFilter filter) {
        log.info("Searching for households with filter: {}", filter);
        Specification<Household> specification = Specification.unrestricted();
        specification = specification.and(withName(filter.getName()));
        specification = specification.and(withCurrency(filter.getCurrency()));
        specification = specification.and(withStatus(filter.getStatus()));
        specification = specification.and(withUUID(filter.getUuid()));
        specification = specification.and(withRefId(filter.getRefId()));
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        List<HouseholdDTO> households = householdRepository.findAll(specification, sort).stream()
                .map(ServiceUtil.HouseholdUtil::buildDTO).toList();
        log.info("Found {} households", households.size());
        return households;
    }

    @Override
    public HouseholdDTO findByRefId(long refId) {
        log.info("Finding household by refId: {}", refId);
        Household household = householdRepository
                .findByRefId(refId)
                .orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("Entity object household not found for refId : %s", refId)));
        log.info("Found household: {}", household);
        return ServiceUtil.HouseholdUtil.buildDTO(household);
    }
}
