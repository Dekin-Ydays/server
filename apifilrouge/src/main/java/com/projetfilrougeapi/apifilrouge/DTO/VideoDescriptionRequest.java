package com.projetfilrougeapi.apifilrouge.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VideoDescriptionRequest {
    @NotBlank
    @Size(max = 500, message = "Description must not exceed 500 characters.")
    public String description;
}
