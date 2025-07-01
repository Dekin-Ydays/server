package com.projetfilrougeapi.apifilrouge.endpoint_api.report;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Report {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "report_id", nullable = false, updatable = false, unique = true)
        private Long id;

        @Enumerated(EnumType.STRING)
        ReportType reportType;

        String description;

        @ManyToOne
        @JoinColumn(name = "sender_id", nullable = false)
        @JsonBackReference("user-reports-sent")
        @JsonIgnore
        private User senderUser;

        @ManyToOne
        @JoinColumn(name = "receiver_id", nullable = false)
        @JsonBackReference("user-reports-received")
        @JsonIgnore
        private User reportedUser;
}
