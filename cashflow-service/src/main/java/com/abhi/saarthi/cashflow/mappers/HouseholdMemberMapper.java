package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.HouseholdMemberDTO;
import com.abhi.saarthi.cashflow.entities.HouseholdMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HouseholdMemberMapper {

    @Mapping(target = "household.refId", source = "householdRefId")
    HouseholdMember toEntity(HouseholdMemberDTO householdMemberDTO);

    @Mapping(target = "householdRefId", source = "household.refId")
    HouseholdMemberDTO toDto(HouseholdMember householdMember);

    void updateEntityFromDto(HouseholdMemberDTO householdMemberDTO, @MappingTarget HouseholdMember householdMember);
}
