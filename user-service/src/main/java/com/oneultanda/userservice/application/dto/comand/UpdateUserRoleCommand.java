package com.oneultanda.userservice.application.dto.comand;

import com.oneultanda.userservice.domain.model.Role;

public record UpdateUserRoleCommand(
        Role role
) {
}