package com.projetfilrougeapi.apifilrouge.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    @NotNull(message = "Report type is required.")
    private String reportType;

    @NotBlank(message = "A description is required for the report.")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters.")
    private String description;

    @NotNull(message = "Sender user ID is required.")
    @Positive(message = "Sender user ID must be a positive number.")
    private Long senderUserId;

    @NotNull(message = "Reported user ID is required.")
    @Positive(message = "Reported user ID must be a positive number.")
    private Long reportedUserId;
}