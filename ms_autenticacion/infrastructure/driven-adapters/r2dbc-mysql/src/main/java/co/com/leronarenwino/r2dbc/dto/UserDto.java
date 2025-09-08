package co.com.leronarenwino.r2dbc.dto;

import co.com.leronarenwino.model.User;
import co.com.leronarenwino.r2dbc.entity.UserEntity;

import java.time.LocalDate;

public record UserDto(
        String name,
        String lastname,
        String email,
        String password,
        Double baseSalary,
        LocalDate birthDate,
        Long idRole,
        String address,
        String telephone
) {
    public static User toDomain(UserEntity entity, String roleName) {
        return new User(
                entity.getNombre(),
                entity.getApellido(),
                entity.getEmail(),
                entity.getClave(),
                entity.getSalarioBase(),
                entity.getFechaNacimiento(),
                entity.getDireccion(),
                entity.getTelefono(),
                roleName
        );
    }

    public static UserEntity toEntity(User user, Long roleId) {
        UserEntity entity = new UserEntity();
        entity.setNombre(user.name());
        entity.setApellido(user.lastname());
        entity.setEmail(user.email());
        entity.setClave(user.password());
        entity.setSalarioBase(user.baseSalary());
        entity.setFechaNacimiento(user.birthDate());
        entity.setIdRol(roleId);
        entity.setDireccion(user.address());
        entity.setTelefono(user.telephone());
        return entity;
    }
}
