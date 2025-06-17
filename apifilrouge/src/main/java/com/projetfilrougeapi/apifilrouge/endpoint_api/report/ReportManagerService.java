package com.projetfilrougeapi.apifilrouge.endpoint_api.report;

import com.projetfilrougeapi.apifilrouge.DTO.ReportRequest;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportManagerService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public ReportManagerService(UserRepository userRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }


    public Boolean banOrNot(User user) {
        if (user.getReportsReceived().size()>10) {
            user.setBanned(true);
            user.setRole(Role.Banned);
            userRepository.save(user);
            return true;
        }else {
            return false;
        }
    }


}
