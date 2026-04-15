package com.projetfilrougeapi.apifilrouge.endpoint_api.video;

import com.projetfilrougeapi.apifilrouge.DTO.VideoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "videos", path = "videos", excerptProjection = VideoProjection.class)
public interface VideoRepository extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {

}
