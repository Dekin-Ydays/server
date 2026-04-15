package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.video.Video;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "videoDetails", types = { Video.class })
public interface VideoProjection {

    Long getId();

    Integer getVideoScore();

    String getUrl();
}
