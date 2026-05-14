package com.abhi.flashcard.controller;

import com.abhi.flashcard.dto.request.CategoryRequest;
import com.abhi.flashcard.dto.response.ApiResponse;
import com.abhi.flashcard.dto.response.CategoryResponse;
import com.abhi.flashcard.dto.response.CategorySummaryResponse;
import com.abhi.flashcard.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcard/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> getAllGenres() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllGenres()));
    }

    @GetMapping("/{refId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable String refId) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryById(refId)));
    }

    @GetMapping("/{refId}/subcategories")
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> getSubCategories(@PathVariable String refId) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getSubCategories(refId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", categoryService.createCategory(request, userId)));
    }

    @PutMapping("/{refId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String refId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Category updated successfully", categoryService.updateCategory(refId, request, userId)));
    }

    @DeleteMapping("/{refId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String refId) {
        categoryService.deleteCategory(refId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
