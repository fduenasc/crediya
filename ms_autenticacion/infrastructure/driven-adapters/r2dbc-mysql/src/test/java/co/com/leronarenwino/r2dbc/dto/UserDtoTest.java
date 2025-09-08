package co.com.leronarenwino.r2dbc.dto;

import co.com.leronarenwino.model.User;
import co.com.leronarenwino.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    @Test
    void constructorAndGettersTest() {
        String name = "Ned";
        String lastname = "Stark";
        String email = "nedstark@winterfell.com";
        String password = "password123";
        Double baseSalary = 5000000.0;
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String address = "Winterfell";
        String telephone = "123456789";
        Long idRole = 1L;

        UserDto userDto = new UserDto(name, lastname, email, password, baseSalary, birthDate, idRole, address, telephone);

        assertThat(userDto.name()).isEqualTo(name);
        assertThat(userDto.lastname()).isEqualTo(lastname);
        assertThat(userDto.email()).isEqualTo(email);
        assertThat(userDto.password()).isEqualTo(password);
        assertThat(userDto.baseSalary()).isEqualTo(baseSalary);
        assertThat(userDto.birthDate()).isEqualTo(birthDate);
        assertThat(userDto.address()).isEqualTo(address);
        assertThat(userDto.telephone()).isEqualTo(telephone);
    }

    @Test
    void equalsAndHashCodeTest() {
        UserDto userDto1 = createUserDto();
        UserDto userDto2 = createUserDto();
        UserDto userDto3 = new UserDto("Different", "User", "different@email.com", "pass", 1000.0, LocalDate.of(1995, 5, 5), 1L, "Winterfell", "ADMIN");

        assertThat(userDto1)
                .isEqualTo(userDto2)
                .isNotEqualTo(userDto3)
                .hasSameHashCodeAs(userDto2)
                .doesNotHaveSameHashCodeAs(userDto3);
    }

    @Test
    void toStringTest() {
        UserDto userDto = createUserDto();

        String expectedToString = "UserDto[name=Ned, lastname=Stark, email=nedstark@winterfell.com, password=password123, baseSalary=5000000.0, birthDate=1990-01-01, idRole=1, address=Winterfell, telephone=123456789]";

        assertThat(userDto).hasToString(expectedToString);
    }

    @Test
    void toEntityTest() {
        User user = createUser();
        Long roleId = 2L;

        UserEntity result = UserDto.toEntity(user, roleId);

        assertThat(result.getNombre()).isEqualTo(user.name());
        assertThat(result.getApellido()).isEqualTo(user.lastname());
        assertThat(result.getEmail()).isEqualTo(user.email());
        assertThat(result.getClave()).isEqualTo(user.password());
        assertThat(result.getSalarioBase()).isEqualTo(user.baseSalary());
        assertThat(result.getFechaNacimiento()).isEqualTo(user.birthDate());
        assertThat(result.getDireccion()).isEqualTo(user.address());
        assertThat(result.getTelefono()).isEqualTo(user.telephone());
        assertThat(result.getIdRol()).isEqualTo(roleId);
    }

    @Test
    void toDomainTest() {
        UserEntity userEntity = createUserEntity();
        String roleName = "CLIENT";

        User result = UserDto.toDomain(userEntity, roleName);

        assertThat(result.name()).isEqualTo(userEntity.getNombre());
        assertThat(result.lastname()).isEqualTo(userEntity.getApellido());
        assertThat(result.email()).isEqualTo(userEntity.getEmail());
        assertThat(result.password()).isEqualTo(userEntity.getClave());
        assertThat(result.baseSalary()).isEqualTo(userEntity.getSalarioBase());
        assertThat(result.birthDate()).isEqualTo(userEntity.getFechaNacimiento());
        assertThat(result.address()).isEqualTo(userEntity.getDireccion());
        assertThat(result.telephone()).isEqualTo(userEntity.getTelefono());
        assertThat(result.role()).isEqualTo(roleName);
    }

    @Test
    void toEntityWithNullValuesTest() {
        User user = new User(null, null, null, null, null, null, null, null, null);
        Long roleId = 1L;

        UserEntity result = UserDto.toEntity(user, roleId);

        assertThat(result.getNombre()).isNull();
        assertThat(result.getApellido()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getClave()).isNull();
        assertThat(result.getSalarioBase()).isNull();
        assertThat(result.getFechaNacimiento()).isNull();
        assertThat(result.getDireccion()).isNull();
        assertThat(result.getTelefono()).isNull();
        assertThat(result.getIdRol()).isEqualTo(roleId);
    }

    @Test
    void toDomainWithNullValuesTest() {
        UserEntity userEntity = new UserEntity();
        String roleName = "CLIENT";

        User result = UserDto.toDomain(userEntity, roleName);

        assertThat(result.name()).isNull();
        assertThat(result.lastname()).isNull();
        assertThat(result.email()).isNull();
        assertThat(result.password()).isNull();
        assertThat(result.baseSalary()).isNull();
        assertThat(result.birthDate()).isNull();
        assertThat(result.address()).isNull();
        assertThat(result.telephone()).isNull();
        assertThat(result.role()).isEqualTo(roleName);
    }

    private UserDto createUserDto() {
        return new UserDto(
                "Ned",
                "Stark",
                "nedstark@winterfell.com",
                "password123",
                5000000.0,
                LocalDate.of(1990, 1, 1),
                1L,
                "Winterfell",
                "123456789"
        );
    }

    private User createUser() {
        return new User(
                "Ned",
                "Stark",
                "nedstark@winterfell.com",
                "password123",
                5000000.0,
                LocalDate.of(1990, 1, 1),
                "Winterfell",
                "123456789",
                "CLIENT"
        );
    }

    private UserEntity createUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setNombre("Ned");
        entity.setApellido("Stark");
        entity.setEmail("nedstark@winterfell.com");
        entity.setClave("password123");
        entity.setSalarioBase(5000000.0);
        entity.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        entity.setDireccion("Winterfell");
        entity.setTelefono("123456789");
        entity.setIdRol(2L);
        return entity;
    }
}