package co.com.leronarenwino.r2dbc.dto;

import co.com.leronarenwino.r2dbc.entity.RoleEntity;

public record RoleDto(
        Long id,
        String name
) {
    public static RoleDto toDto(RoleEntity roleEntity) {
        return new RoleDto(
                roleEntity.getUniqueID(),
                roleEntity.getNombre()
        );
    }
}
