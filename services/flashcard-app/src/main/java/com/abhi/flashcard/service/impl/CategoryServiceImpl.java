package com.abhi.flashcard.service.impl;

import com.abhi.flashcard.dto.request.CategoryRequest;
import com.abhi.flashcard.dto.response.CategoryResponse;
import com.abhi.flashcard.dto.response.CategorySummaryResponse;
import com.abhi.flashcard.entity.Category;
import com.abhi.flashcard.exception.BadRequestException;
import com.abhi.flashcard.exception.ResourceNotFoundException;
import com.abhi.flashcard.repository.CategoryRepository;
import com.abhi.flashcard.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategorySummaryResponse> getAllGenres() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Override
    public List<CategorySummaryResponse> getSubCategories(String refId) {
        categoryRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", refId));
        return categoryRepository.findByParentRefId(refId).stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(String refId) {
        return toResponse(categoryRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", refId)));
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        validateUniqueName(request.getName(), request.getParentRefId());
        Category parent = resolveParent(request.getParentRefId());
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .parent(parent)
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(String refId, CategoryRequest request) {
        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", refId));
        String currentParentRefId = category.getParent() != null ? category.getParent().getRefId() : null;
        boolean nameChanged = !category.getName().equals(request.getName());
        boolean parentChanged = !Objects.equals(currentParentRefId, request.getParentRefId());
        if (nameChanged || parentChanged) {
            validateUniqueName(request.getName(), request.getParentRefId());
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParent(resolveParent(request.getParentRefId()));
        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(String refId) {
        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", refId));
        if (!category.getSubCategories().isEmpty()) {
            throw new BadRequestException("Cannot delete category with sub-categories. Remove sub-categories first.");
        }
        if (!category.getDecks().isEmpty()) {
            throw new BadRequestException("Cannot delete category that has decks assigned. Reassign or remove decks first.");
        }
        categoryRepository.delete(category);
    }

    private void validateUniqueName(String name, String parentRefId) {
        boolean exists = parentRefId == null
                ? categoryRepository.existsByNameAndParentIsNull(name)
                : categoryRepository.existsByNameAndParentRefId(name, parentRefId);
        if (exists) {
            throw new BadRequestException("A category named '" + name + "' already exists at this level.");
        }
    }

    private Category resolveParent(String parentRefId) {
        if (parentRefId == null) return null;
        return categoryRepository.findByRefId(parentRefId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category", parentRefId));
    }

    private CategorySummaryResponse toSummaryResponse(Category c) {
        return CategorySummaryResponse.builder()
                .refId(c.getRefId())
                .name(c.getName())
                .description(c.getDescription())
                .parentRefId(c.getParent() != null ? c.getParent().getRefId() : null)
                .parentName(c.getParent() != null ? c.getParent().getName() : null)
                .subCategoryCount(c.getSubCategories().size())
                .deckCount(c.getDecks().size())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .refId(c.getRefId())
                .name(c.getName())
                .description(c.getDescription())
                .parentRefId(c.getParent() != null ? c.getParent().getRefId() : null)
                .parentName(c.getParent() != null ? c.getParent().getName() : null)
                .subCategories(c.getSubCategories().stream().map(this::toSummaryResponse).toList())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
