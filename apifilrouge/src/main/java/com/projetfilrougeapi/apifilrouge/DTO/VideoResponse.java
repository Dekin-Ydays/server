package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.video.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    private Long id;
    private String description;
    private String url;
    private Integer score;

    public static VideoResponse fromEntity(Video video) {

        return VideoResponse.builder()
                .id(video.getId())
                .description(video.getVideoDescription())
                .url(video.getUrl())
                .score(video.getVideoScore())
                .build();
    }
}
