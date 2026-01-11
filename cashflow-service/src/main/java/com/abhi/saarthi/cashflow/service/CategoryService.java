package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.CategoryDTO;
import com.abhi.saarthi.cashflow.model.CategorySearchFilter;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> add(List<CategoryDTO> dtoList);

    List<CategoryDTO> update(List<CategoryDTO> dtoList);

    void delete(List<CategoryDTO> dtoList);

    CategoryDTO findByRefId(long refId);

    List<CategoryDTO> search(CategorySearchFilter filter);
}
