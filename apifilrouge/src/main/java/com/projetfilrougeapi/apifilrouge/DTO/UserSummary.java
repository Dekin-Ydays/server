package com.projetfilrougeapi.apifilrouge.DTO;

import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummary {
    private Long id;
    private String pseudo;
    private String imageUrl;

    public static UserSummary fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserSummary(
                user.getId(),
                user.getPseudo(),
                user.getImageUrl()
        );
    }
}