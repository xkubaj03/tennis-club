package com.inqool.tennisclub.data.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum AuthorizationType {
    USER("USER"),
    ADMIN("ADMIN", Set.of(USER));

    private final String value;
    private final Set<AuthorizationType> impliedRoles;

    AuthorizationType(String value) {
        this.value = value;
        this.impliedRoles = Collections.emptySet();
    }

    AuthorizationType(String value, Set<AuthorizationType> impliedRoles) {
        this.value = value;
        this.impliedRoles = impliedRoles;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String toSpringRole() {
        return "ROLE_" + this.value;
    }

    public Set<AuthorizationType> getAllRoles() {
        Set<AuthorizationType> result = EnumSet.of(this);
        for (AuthorizationType implied : impliedRoles) {
            result.addAll(implied.getAllRoles());
        }
        return result;
    }
}
