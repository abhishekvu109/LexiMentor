package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.constants.Status;
import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.entities.Household;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = KeyGeneratorUtil.class, imports = KeyGeneratorUtil.class)
public interface HouseholdMapper {
    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    @Mapping(target = "currency", expression = "java(Currency.parse(householdDTO.getCurrency()))")
    @Mapping(target = "status", expression = "java(Status.ApplicationStatus.getStatus(householdDTO.getStatus()))")
    Household toEntity(HouseholdDTO householdDTO);


    HouseholdDTO toDto(Household household);

    void updateEntityFromDto(HouseholdDTO householdDTO, @MappingTarget Household household);
}
