package com.projetfilrougeapi.apifilrouge.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private Long ticketId;
    private String content;
    private Double rating;
    private Long senderUserId;
    private Long reviewedUserId;

}