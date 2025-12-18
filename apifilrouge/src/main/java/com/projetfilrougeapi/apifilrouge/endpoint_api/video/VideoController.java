package com.projetfilrougeapi.apifilrouge.endpoint_api.video;

import com.projetfilrougeapi.apifilrouge.DTO.VideoDescriptionRequest;
import com.projetfilrougeapi.apifilrouge.DTO.VideoResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/{id}")
    public EntityModel<VideoResponse> getVideoById(@PathVariable("id") Long id) {
        return videoService.getVideoById(id);
    }

    @GetMapping
    public CollectionModel<EntityModel<VideoResponse>> getAllVideos() {
        return videoService.getAllVideos();
    }

    @PatchMapping("/{id}")
    public EntityModel<VideoResponse> updateVideoDescription(@PathVariable("id") Long id, @RequestBody VideoDescriptionRequest request) {
        return videoService.updateVideoDescription(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideo(@PathVariable("id") Long id) {
        videoService.deleteVideo(id);
    }

}
