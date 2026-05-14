package com.abhi.flashcard.service;

import com.abhi.flashcard.dto.request.CategoryRequest;
import com.abhi.flashcard.dto.response.CategoryResponse;
import com.abhi.flashcard.dto.response.CategorySummaryResponse;

import java.util.List;

public interface CategoryService {

    List<CategorySummaryResponse> getAllGenres();

    List<CategorySummaryResponse> getSubCategories(String refId);

    CategoryResponse getCategoryById(String refId);

    CategoryResponse createCategory(CategoryRequest request, String userId);

    CategoryResponse updateCategory(String refId, CategoryRequest request, String userId);

    void deleteCategory(String refId);
}
