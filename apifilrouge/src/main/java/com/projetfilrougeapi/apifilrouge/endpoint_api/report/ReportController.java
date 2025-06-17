package com.projetfilrougeapi.apifilrouge.endpoint_api.report;

import com.projetfilrougeapi.apifilrouge.DTO.ReportRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {
    ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Report> createReport(@RequestBody ReportRequest report) {
        return reportService.createReport(report);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Report> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    @GetMapping
    public CollectionModel<EntityModel<Report>> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}/senderUser")
    public EntityModel<User> getSenderUserByReportId(@PathVariable Long id) {
        return reportService.getSenderUserByReportId(id);
    }

    @GetMapping("/{id}/reportedUser")
    public EntityModel<User> getReportedUserByReportId(@PathVariable Long id) {
        return reportService.getReportedUserByReportId(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
    }

}
