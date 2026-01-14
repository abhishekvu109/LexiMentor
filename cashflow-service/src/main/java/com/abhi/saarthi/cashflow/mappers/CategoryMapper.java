package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.CategoryDTO;
import com.abhi.saarthi.cashflow.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryDTO categoryDTO);

    CategoryDTO toDto(Category category);

    void updateEntityFromDto(CategoryDTO categoryDTO, @MappingTarget Category category);
}
