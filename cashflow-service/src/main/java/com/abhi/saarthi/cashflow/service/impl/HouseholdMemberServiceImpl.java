package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.constants.Status;
import com.abhi.saarthi.cashflow.dto.HouseholdMemberDTO;
import com.abhi.saarthi.cashflow.entities.HouseholdMember;
import com.abhi.saarthi.cashflow.exceptions.entities.ServerException;
import com.abhi.saarthi.cashflow.model.HouseholdMemberSearchFilter;
import com.abhi.saarthi.cashflow.repository.HouseholdMemberRepository;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import com.abhi.saarthi.cashflow.service.HouseholdMemberService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HouseholdMemberServiceImpl implements HouseholdMemberService {

    private final HouseholdMemberRepository memberRepository;
    private final HouseholdRepository householdRepository;

    @Override
    @Transactional
    public List<HouseholdMemberDTO> add(List<HouseholdMemberDTO> dtos) {
        log.info("Adding new household members: {}", dtos);
        List<HouseholdMember> householdMembers = dtos.stream().map(dto -> {
            HouseholdMember householdMember = ServiceUtil.HouseholdMemberUtil.buildEntity(dto);
            householdMember.setHousehold(householdRepository.findByRefId(Long.parseLong(dto.getHouseholdRefId()))
                    .orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getHouseholdRefId()))));
            return householdMember;
        }).toList();
        householdMembers = memberRepository.saveAll(householdMembers);
        log.info("Successfully added new household members: {}", householdMembers);
        return householdMembers.stream().map(ServiceUtil.HouseholdMemberUtil::buildDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<HouseholdMemberDTO> update(List<HouseholdMemberDTO> dtos) {
        log.info("Updating household members: {}", dtos);
        List<HouseholdMember> householdMembers = dtos.stream().map(dto -> {
            HouseholdMember householdMember = memberRepository.findByRefId(Long.parseLong(dto.getRefId()))
                    .orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("Entity object household member not found for refId : %s", dto.getRefId())));
            ServiceUtil.HouseholdMemberUtil.updateEntity(householdMember, dto);
            householdMember.setHousehold(householdRepository.findByRefId(Long.parseLong(dto.getHouseholdRefId()))
                    .orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getHouseholdRefId()))));
            return householdMember;
        }).toList();
        householdMembers = memberRepository.saveAll(householdMembers);
        log.info("Successfully updated household members: {}", householdMembers);
        return householdMembers.stream().map(ServiceUtil.HouseholdMemberUtil::buildDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(List<HouseholdMemberDTO> dtos) {
        log.info("Deleting household members: {}", dtos);
        List<HouseholdMember> householdMembers = dtos.stream().map(dto -> memberRepository.findByRefId(Long.parseLong(dto.getRefId()))
                .orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("Entity object household member not found for refId : %s", dto.getRefId())))
        ).toList();
        memberRepository.deleteAll(householdMembers);
        log.info("Successfully deleted household members");
    }

    @Override
    @Transactional
    public void delete(long refId) {
        log.info("Deleting household member by refId: {}", refId);
        HouseholdMember householdMember = memberRepository.findByRefId(refId)
                .orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("Entity object household member not found for refId : %s", refId)));
        memberRepository.delete(householdMember);
        log.info("Successfully deleted household member with refId: {}", refId);
    }

    @Override
    public List<HouseholdMemberDTO> search(HouseholdMemberSearchFilter filter) {
        log.info("Searching for household members with filter: {}", filter);
        Specification<HouseholdMember> specification = Specification.unrestricted();
        specification = (StringUtils.isNotEmpty(filter.getUuid())) ? specification.and(((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid()))) : specification;
        specification = (StringUtils.isNotEmpty(filter.getRefId())) ? specification.and(((root, query, cb) -> cb.equal(root.get("refId"), Long.parseLong(filter.getRefId())))) : specification;
        specification = (StringUtils.isNotEmpty(filter.getUser())) ? specification.and(((root, query, cb) -> cb.equal(root.get("user"), filter.getUser()))) : specification;
        specification = (StringUtils.isNotEmpty(filter.getRole())) ? specification.and(((root, query, cb) -> cb.equal(root.get("role"), filter.getRole()))) : specification;
        specification = (StringUtils.isNotEmpty(filter.getStatus())) ? specification.and(((root, query, cb) -> cb.equal(root.get("status"), Status.ApplicationStatus.getStatus(filter.getStatus())))) : specification;
        specification = (filter.getJoiningDateFrom() != null && filter.getJoiningDateTo() != null) ? specification.and(((root, query, cb) -> cb.between(root.get("joiningDate"), filter.getJoiningDateFrom(), filter.getJoiningDateTo()))) : specification;
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        List<HouseholdMemberDTO> members = memberRepository.findAll(specification, sort).stream()
                .map(ServiceUtil.HouseholdMemberUtil::buildDTO).toList();
        log.info("Found {} household members", members.size());
        return members;
    }


    @Override
    public HouseholdMemberDTO get(long refId) {
        log.info("Finding household member by refId: {}", refId);
        HouseholdMember householdMember = memberRepository.findByRefId(refId).orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("HouseholdMember object not found for refId:%s", refId)));
        log.info("Found household member: {}", householdMember);
        return ServiceUtil.HouseholdMemberUtil.buildDTO(householdMember);
    }

    @Override
    public HouseholdMemberDTO get(String uuid) {
        log.info("Finding household member by uuid: {}", uuid);
        HouseholdMember householdMember = memberRepository.findByUuid(uuid).orElseThrow(() -> new ServerException().new EntityObjectNotFound(String.format("HouseholdMember object not found for uuid:%s", uuid)));
        log.info("Found household member: {}", householdMember);
        return ServiceUtil.HouseholdMemberUtil.buildDTO(householdMember);
    }

    @Override
    public HouseholdMemberDTO findByRefId(long refId) {
        return get(refId);
    }
}
