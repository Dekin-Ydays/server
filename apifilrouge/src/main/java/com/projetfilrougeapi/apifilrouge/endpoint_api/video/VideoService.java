package com.projetfilrougeapi.apifilrouge.endpoint_api.video;

import com.projetfilrougeapi.apifilrouge.DTO.VideoDescriptionRequest;
import com.projetfilrougeapi.apifilrouge.DTO.VideoResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class VideoService {
    private final  VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public EntityModel<VideoResponse> getVideoById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        VideoResponse response = VideoResponse.fromEntity(video);
        return EntityModel.of(response,
                linkTo(methodOn(VideoController.class).getVideoById(id)).withSelfRel());
    }

    public CollectionModel<EntityModel<VideoResponse>> getAllVideos() {
        List<EntityModel<VideoResponse>> videos = videoRepository.findAll().stream()
                .map(video -> {
                    VideoResponse response = VideoResponse.fromEntity(video);
                    return EntityModel.of(response,
                            linkTo(methodOn(VideoController.class).getVideoById(video.getId())).withSelfRel());
                })
                .collect(Collectors.toList());
        return CollectionModel.of(videos,
                linkTo(methodOn(VideoController.class).getAllVideos()).withSelfRel());
    }

    public EntityModel<VideoResponse> updateVideoDescription(Long id, VideoDescriptionRequest request) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        video.setVideoDescription(request.description);
        videoRepository.save(video);

        VideoResponse response = VideoResponse.fromEntity(video);
        return EntityModel.of(response,
                linkTo(methodOn(VideoController.class).getVideoById(id)).withSelfRel());
    }

    public void deleteVideo(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        videoRepository.delete(video);
    }
}

