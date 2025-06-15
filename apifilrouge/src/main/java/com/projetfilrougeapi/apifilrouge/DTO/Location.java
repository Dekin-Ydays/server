package com.projetfilrougeapi.apifilrouge.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Location {
    private Double latitude;
    private Double longitude;
}