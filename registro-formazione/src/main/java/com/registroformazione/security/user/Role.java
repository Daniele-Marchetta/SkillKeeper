package com.registroformazione.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.registroformazione.security.user.Permission.*;

@RequiredArgsConstructor
public enum Role {

  GUEST(   
          Set.of(
                  GUEST_READ,
                  GUEST_UPDATE,
                  GUEST_CREATE,
                  GUEST_DELETE
          )  
          
          ),
  HR(
          Set.of(
                  HR_READ,
                  HR_UPDATE,
                  HR_DELETE,
                  HR_CREATE
          )
  ) ;

  @Getter
  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities = getPermissions()
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermessi()))
            .collect(Collectors.toList());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return authorities;
  }
}
