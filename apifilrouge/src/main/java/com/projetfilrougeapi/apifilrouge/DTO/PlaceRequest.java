package com.projetfilrougeapi.apifilrouge.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequest {
    private String name;
    private String description;
    private String address;
    private Long cityId;
}
