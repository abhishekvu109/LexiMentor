package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.EarningDTO;
import com.abhi.saarthi.cashflow.entities.Earning;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {KeyGeneratorUtil.class}, imports = KeyGeneratorUtil.class)
public interface EarningMapper {

    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    @Mapping(target = "status", constant = "1")
    Earning toEntity(EarningDTO dto);


    @Mapping(target = "status", expression = "java(com.abhi.saarthi.cashflow.constants.Status.ApplicationStatus.getStatusStr(earning.getStatus()))")
    EarningDTO toDTO(Earning earning);
}
