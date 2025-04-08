package com.oneultanda.userservice.application.dto.comand;

import com.oneultanda.userservice.domain.entity.Role;

public record UpdateUserRoleCommand(
        Role role
) {
}