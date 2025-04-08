package com.oneultanda.userservice.presentaion.dto.request;

import com.oneultanda.userservice.application.dto.comand.UpdateUserCommand;
import com.oneultanda.userservice.application.dto.comand.UpdateUserRoleCommand;
import com.oneultanda.userservice.domain.entity.Role;

public record UpdateUserRoleRequest(
        Role role
) {
    public UpdateUserRoleCommand toCommand() {
        return new UpdateUserRoleCommand(role);
    }
}
