package com.registroformazione.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    HR_READ("hr:read"),
    HR_UPDATE("hr:update"),
    HR_CREATE("hr:create"),
    HR_DELETE("hr:delete"),
    GUEST_READ("guest:read"),
    GUEST_UPDATE("guest:update"),
    GUEST_CREATE("guest:create"),
    GUEST_DELETE("guest:delete")

    ;

    @Getter
    private final String permessi;
}
