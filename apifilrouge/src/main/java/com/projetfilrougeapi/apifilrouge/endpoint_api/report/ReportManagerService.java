package com.projetfilrougeapi.apifilrouge.endpoint_api.report;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.Role;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportManagerService {
    private final UserRepository userRepository;

    public ReportManagerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Boolean banOrNot(User user) {
        if (user.getReportsReceived().size()>10 && user.getRole()!=Role.Admin && user.getRole()!=Role.AuthService) {
            user.setBanned(true);
            user.setRole(Role.Banned);
            userRepository.save(user);
            return true;
        }else {
            return false;
        }
    }
}