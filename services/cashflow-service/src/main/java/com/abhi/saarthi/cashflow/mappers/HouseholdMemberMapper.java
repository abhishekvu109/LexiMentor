package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.HouseholdMemberDTO;
import com.abhi.saarthi.cashflow.entities.HouseholdMember;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = KeyGeneratorUtil.class, imports = KeyGeneratorUtil.class)
public interface HouseholdMemberMapper {

//    @Mapping(target = "household.refId", source = "householdRefId")
    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    @Mapping(target = "status", constant = "1")
    HouseholdMember toEntity(HouseholdMemberDTO householdMemberDTO);

    @Mapping(target = "householdRefId", source = "household.refId")
    HouseholdMemberDTO toDto(HouseholdMember householdMember);

    void updateEntityFromDto(HouseholdMemberDTO householdMemberDTO, @MappingTarget HouseholdMember householdMember);
}
