package com.projetfilrougeapi.apifilrouge.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    @NotBlank(message = "First name cannot be empty.")
    @Size(max = 100, message = "First name must not exceed 100 characters.")
    private String name;

    @NotBlank(message = "Last name cannot be empty.")
    @Size(max = 100, message = "Last name must not exceed 100 characters.")
    private String lastName;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    @PositiveOrZero(message = "Unit price cannot be negative.")
    private Double unitPrice;

    @NotNull(message = "Order ID is required.")
    @Positive(message = "Order ID must be a positive number.")
    private Long orderId;
}