package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.UpdateUserRoleCommand;
import com.oneultanda.userservice.domain.model.Role;

public record UpdateUserRoleRequest(
        Role role
) {
    public UpdateUserRoleCommand toCommand() {
        return new UpdateUserRoleCommand(role);
    }
}
