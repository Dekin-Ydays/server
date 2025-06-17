package com.projetfilrougeapi.apifilrouge.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private String reportType;
    private String description;
    private Long senderUserId;
    private Long reportedUserId;
}
