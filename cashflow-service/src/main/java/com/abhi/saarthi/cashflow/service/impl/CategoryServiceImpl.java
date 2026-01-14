package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.constants.Status;
import com.abhi.saarthi.cashflow.dto.CategoryDTO;
import com.abhi.saarthi.cashflow.entities.Category;
import com.abhi.saarthi.cashflow.exceptions.entities.ServerException;
import com.abhi.saarthi.cashflow.mappers.CategoryMapper;
import com.abhi.saarthi.cashflow.model.CategorySearchFilter;
import com.abhi.saarthi.cashflow.repository.CategoryRepository;
import com.abhi.saarthi.cashflow.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public List<CategoryDTO> add(List<CategoryDTO> dtoList) {
        log.info("Adding new categories: {}", dtoList);
        List<Category> categories = dtoList.stream().map(categoryMapper::toEntity).toList();
        categories = categoryRepository.saveAll(categories);
        log.info("Successfully added new categories: {}", categories);
        return categories.stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CategoryDTO> update(List<CategoryDTO> dtoList) {
        log.info("Updating categories: {}", dtoList);
        List<Category> categories = dtoList.stream().map(dto -> {
            Category category = categoryRepository
                    .findByRefId(Long.parseLong(dto.getRefId()))
                    .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object category not found for refId : %s", dto.getRefId())));
            categoryMapper.updateEntityFromDto(dto, category);
            return category;
        }).toList();
        categories = categoryRepository.saveAll(categories);
        log.info("Successfully updated categories: {}", categories);
        return categories.stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(List<CategoryDTO> dtoList) {
        log.info("Deleting categories: {}", dtoList);
        List<Category> categories = dtoList.stream().map(dto -> categoryRepository
                .findByRefId(Long.parseLong(dto.getRefId()))
                .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object category not found for refId : %s", dto.getRefId())))).toList();
        categoryRepository.deleteAll(categories);
        log.info("Successfully deleted categories");
    }

    @Override
    public CategoryDTO findByRefId(long refId) {
        log.info("Finding category by refId: {}", refId);
        Category category = categoryRepository
                .findByRefId(refId)
                .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object category not found for refId : %s", refId)));
        log.info("Found category: {}", category);
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDTO> search(CategorySearchFilter filter) {
        log.info("Searching for categories with filter: {}", filter);
        Specification<Category> spec = Specification.unrestricted();
        spec = (StringUtils.isNotEmpty(filter.getUuid())) ? spec.and(((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid()))) : spec;
        spec = (StringUtils.isNotEmpty(filter.getRefId())) ? spec.and(((root, query, cb) -> cb.equal(root.get("refId"), Long.parseLong(filter.getRefId())))) : spec;
        spec = (StringUtils.isNotEmpty(filter.getName())) ? spec.and(((root, query, cb) -> cb.like(root.get("name"), filter.getName()))) : spec;
        spec = (StringUtils.isNotEmpty(filter.getStatus())) ? spec.and(((root, query, cb) -> cb.equal(root.get("status"), Status.ApplicationStatus.getStatus(filter.getStatus())))) : spec;
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        List<CategoryDTO> categories = categoryRepository.findAll(spec,sort).stream()
                .map(categoryMapper::toDto).toList();
        log.info("Found {} categories", categories.size());
        return categories;
    }
}
