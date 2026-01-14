package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.entities.Household;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HouseholdMapper {

    Household toEntity(HouseholdDTO householdDTO);

    HouseholdDTO toDto(Household household);

    void updateEntityFromDto(HouseholdDTO householdDTO, @MappingTarget Household household);
}
