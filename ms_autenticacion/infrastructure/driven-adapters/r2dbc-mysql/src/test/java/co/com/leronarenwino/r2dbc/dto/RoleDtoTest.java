package co.com.leronarenwino.r2dbc.dto;

import co.com.leronarenwino.r2dbc.entity.RoleEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleDtoTest {

    @Test
    void constructorAndGettersTest() {
        Long id = 1L;
        String name = "ADMIN";

        RoleDto roleDto = new RoleDto(id, name);

        assertThat(roleDto.id()).isEqualTo(id);
        assertThat(roleDto.name()).isEqualTo(name);
    }

    @Test
    void equalsAndHashCodeTest() {
        RoleDto roleDto1 = new RoleDto(1L, "ADMIN");
        RoleDto roleDto2 = new RoleDto(1L, "ADMIN");
        RoleDto roleDto3 = new RoleDto(2L, "CLIENT");

        assertThat(roleDto1)
                .isEqualTo(roleDto2)
                .isNotEqualTo(roleDto3)
                .hasSameHashCodeAs(roleDto2)
                .doesNotHaveSameHashCodeAs(roleDto3);
    }

    @Test
    void toStringTest() {
        RoleDto roleDto = new RoleDto(1L, "ADMIN");

        assertThat(roleDto)
                .hasToString("RoleDto[id=1, name=ADMIN]")
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.name()).isEqualTo("ADMIN");
                });
    }

    @Test
    void toDtoTest() {
        RoleEntity entity = new RoleEntity();
        entity.setUniqueID(1L);
        entity.setNombre("CLIENT");

        RoleDto result = RoleDto.toDto(entity);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("CLIENT");
    }

    @Test
    void toDtoWithNullValuesTest() {
        RoleEntity entity = new RoleEntity();
        entity.setUniqueID(null);
        entity.setNombre(null);
        entity.setDescripcion(null);

        RoleDto result = RoleDto.toDto(entity);

        assertThat(result.id()).isNull();
        assertThat(result.name()).isNull();
    }
}