package com.projetfilrougeapi.apifilrouge.endpoint_api.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
@Repository
public interface ReportRepository  extends JpaRepository<Report, Long> {
    Optional<Report> findById(Long id) ;
}
