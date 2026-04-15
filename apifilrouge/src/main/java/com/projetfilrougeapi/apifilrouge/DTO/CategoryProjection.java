package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.category.Category;
import com.projetfilrougeapi.apifilrouge.endpoint_api.category.TypeCategory;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "categoryDetails", types = { Category.class })
public interface CategoryProjection {
    Long getId();

    String getCategoryName();

    String getCategoryDescription();

    TypeCategory getTypeCategory();
}
