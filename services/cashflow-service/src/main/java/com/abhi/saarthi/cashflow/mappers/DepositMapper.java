package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.DepositDTO;
import com.abhi.saarthi.cashflow.entities.Deposit;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {KeyGeneratorUtil.class}, imports = KeyGeneratorUtil.class)
public interface DepositMapper {
    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    @Mapping(target = "status", constant = "1")
    @Mapping(target = "depositDate", source = "depositDate")
    @Mapping(target = "household.refId", source = "householdRefId")
    Deposit toEntity(DepositDTO dto);


    @Mapping(target = "householdRefId", source = "household.refId")
    @Mapping(target = "status", expression = "java(com.abhi.saarthi.cashflow.constants.Status.ApplicationStatus.getStatusStr(deposit.getStatus()))")
    DepositDTO toDTO(Deposit deposit);
}
