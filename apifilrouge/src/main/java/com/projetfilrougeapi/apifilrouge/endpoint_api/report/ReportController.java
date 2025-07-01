package com.projetfilrougeapi.apifilrouge.endpoint_api.report;

import com.projetfilrougeapi.apifilrouge.DTO.ReportRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Report> createReport(@Valid @RequestBody ReportRequest report) {
        return reportService.createReport(report);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Report> getReportById(@PathVariable("id") Long id) {
        return reportService.getReportById(id);
    }

    @GetMapping
    public CollectionModel<EntityModel<Report>> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}/sender-user")
    public EntityModel<User> getSenderUserByReportId(@PathVariable("id") Long id) {
        return reportService.getSenderUserByReportId(id);
    }

    @GetMapping("/{id}/reported-user")
    public EntityModel<User> getReportedUserByReportId(@PathVariable("id") Long id) {
        return reportService.getReportedUserByReportId(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteReport(@PathVariable("id") Long id) {
        reportService.deleteReport(id);
    }

}
