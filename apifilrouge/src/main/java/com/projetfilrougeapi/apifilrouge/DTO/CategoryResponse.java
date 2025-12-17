package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private TypeCategory type;

    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getCategoryName())
                .description(category.getCategoryDescription())
                .type(category.getTypeCategory())
                .build();
    }
}
