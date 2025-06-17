package com.projetfilrougeapi.apifilrouge.endpoint_api.user;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import static com.projetfilrougeapi.apifilrouge.endpoint_api.user.Permission.*;

@RequiredArgsConstructor
public enum Role {
    AuthService(Set.of(
            AUTH_SERVICE_CREATE,
            AUTH_SERVICE_READ,
            AUTH_SERVICE_UPDATE,
            AUTH_SERVICE_DELETE
    )),
    User(Set.of(
            USER_READ,
            USER_CREATE,
            USER_UPDATE,
            USER_DELETE
    )),
    Admin(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE

            )
    ),
    Organizer(
            Set.of(
                    ORGANIZER_CREATE,
                    ORGANIZER_READ,
                    ORGANIZER_UPDATE,
                    ORGANIZER_DELETE
            )
    ),
    Banned(
            Collections.emptySet()
    );



    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}