package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.CategoryDTO;
import com.abhi.saarthi.cashflow.entities.Category;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = KeyGeneratorUtil.class, imports = KeyGeneratorUtil.class)
public interface CategoryMapper {

    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    @Mapping(target = "status", constant = "1")
    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(target = "status",expression = "java(com.abhi.saarthi.cashflow.constants.Status.ApplicationStatus.getStatusStr(category.getStatus()))")
    CategoryDTO toDto(Category category);

    void updateEntityFromDto(CategoryDTO categoryDTO, @MappingTarget Category category);
}
