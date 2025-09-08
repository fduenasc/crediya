package co.com.leronarenwino.r2dbc;

import co.com.leronarenwino.model.User;
import co.com.leronarenwino.r2dbc.entity.RoleEntity;
import co.com.leronarenwino.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class UserRepositoryAdapterTest {

    @Mock
    private UserR2dbcRepository userR2dbcRepository;

    @Mock
    private RoleR2dbcRepository roleR2dbcRepository;

    private UserRepositoryAdapter userRepositoryAdapter;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        userRepositoryAdapter = new UserRepositoryAdapter(userR2dbcRepository, roleR2dbcRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void saveShouldSaveUserSuccessfullyTest() {
        User user = createValidUser();
        RoleEntity roleEntity = createRoleEntity();
        UserEntity userEntity = createUserEntity();

        when(roleR2dbcRepository.findAll()).thenReturn(Flux.just(roleEntity));
        when(userR2dbcRepository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));

        StepVerifier.create(userRepositoryAdapter.save(user))
                .verifyComplete();
    }

    @Test
    void saveShouldFailWhenRoleNotFoundTest() {
        User user = createValidUser();

        when(roleR2dbcRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userRepositoryAdapter.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Role not found"))
                .verify();
    }

    @Test
    void findUserByEmailShouldReturnUserWhenExistsTest() {
        String email = "nedstark@winterfell.com";
        UserEntity userEntity = createUserEntity();
        RoleEntity roleEntity = createRoleEntity();

        when(userR2dbcRepository.findAll()).thenReturn(Flux.just(userEntity));
        when(roleR2dbcRepository.findById(anyLong())).thenReturn(Mono.just(roleEntity));

        StepVerifier.create(userRepositoryAdapter.findUserByEmail(email))
                .expectNextMatches(user ->
                        user.email().equals(email) &&
                                user.name().equals("Ned") &&
                                user.role().equals("CLIENT"))
                .verifyComplete();
    }

    @Test
    void findUserByEmailShouldReturnEmptyWhenNotExistsTest() {
        String email = "nonexistent@email.com";

        when(userR2dbcRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userRepositoryAdapter.findUserByEmail(email))
                .verifyComplete();
    }

    @Test
    void existsByEmailShouldReturnTrueWhenUserExistsTest() {
        String email = "nedstark@winterfell.com";
        UserEntity userEntity = createUserEntity();
        RoleEntity roleEntity = createRoleEntity();

        when(userR2dbcRepository.findAll()).thenReturn(Flux.just(userEntity));
        when(roleR2dbcRepository.findById(anyLong())).thenReturn(Mono.just(roleEntity));

        StepVerifier.create(userRepositoryAdapter.existsByEmail(email))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmailShouldReturnFalseWhenUserNotExistsTest() {
        String email = "nonexistent@email.com";

        when(userR2dbcRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userRepositoryAdapter.existsByEmail(email))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void saveShouldHandleRepositoryErrorTest() {
        User user = createValidUser();
        RoleEntity roleEntity = createRoleEntity();

        when(roleR2dbcRepository.findAll()).thenReturn(Flux.just(roleEntity));
        when(userR2dbcRepository.save(any(UserEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(userRepositoryAdapter.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    void findUserByEmailShouldHandleRoleNotFoundTest() {
        String email = "nedstark@winterfell.com";
        UserEntity userEntity = createUserEntity();

        when(userR2dbcRepository.findAll()).thenReturn(Flux.just(userEntity));
        when(roleR2dbcRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.findUserByEmail(email))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalStateException &&
                                throwable.getMessage().equals("Role name cannot be null"))
                .verify();
    }

    @Test
    void mapToUserShouldHandleRoleRepositoryErrorTest() {
        String email = "nedstark@winterfell.com";
        UserEntity userEntity = createUserEntity();

        when(userR2dbcRepository.findAll()).thenReturn(Flux.just(userEntity));
        when(roleR2dbcRepository.findById(anyLong()))
                .thenReturn(Mono.error(new RuntimeException("Role repository error")));

        StepVerifier.create(userRepositoryAdapter.findUserByEmail(email))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Role repository error"))
                .verify();
    }

    @Test
    void getRoleIdByNameShouldHandleCaseInsensitiveMatchTest() {
        User user = new User(
                "Ned",
                "Stark",
                "nedstark@winterfell.com",
                "password",
                5000000.0,
                LocalDate.of(1990, 1, 1),
                "Winterfell, The North",
                "123456789",
                "client"
        );

        RoleEntity roleEntity = createRoleEntity();
        roleEntity.setNombre("CLIENT");
        UserEntity userEntity = createUserEntity();

        when(roleR2dbcRepository.findAll()).thenReturn(Flux.just(roleEntity));
        when(userR2dbcRepository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));

        StepVerifier.create(userRepositoryAdapter.save(user))
                .verifyComplete();
    }

    @Test
    void findUserByEmailShouldHandleUserRepositoryErrorTest() {
        String email = "nedstark@winterfell.com";

        when(userR2dbcRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("User repository error")));

        StepVerifier.create(userRepositoryAdapter.findUserByEmail(email))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("User repository error"))
                .verify();
    }

    @Test
    void existsByEmailShouldHandleRepositoryErrorTest() {
        String email = "nedstark@winterfell.com";

        when(userR2dbcRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Repository error")));

        StepVerifier.create(userRepositoryAdapter.existsByEmail(email))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Repository error"))
                .verify();
    }

    @Test
    void saveShouldHandleRoleRepositoryErrorTest() {
        User user = createValidUser();

        when(roleR2dbcRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Role repository error")));

        StepVerifier.create(userRepositoryAdapter.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Role repository error"))
                .verify();
    }

    @Test
    void findUserByEmailShouldHandleMultipleUsersWithSameEmailTest() {
        String email = "nedstark@winterfell.com";
        UserEntity userEntity1 = createUserEntity();
        UserEntity userEntity2 = createUserEntity();
        userEntity2.setId(2L);
        RoleEntity roleEntity = createRoleEntity();

        when(userR2dbcRepository.findAll()).thenReturn(Flux.just(userEntity1, userEntity2));
        when(roleR2dbcRepository.findById(anyLong())).thenReturn(Mono.just(roleEntity));

        StepVerifier.create(userRepositoryAdapter.findUserByEmail(email))
                .expectNextMatches(user ->
                        user.email().equals(email) &&
                                user.name().equals("Ned"))
                .verifyComplete();
    }


    @Test
    void saveUserWithNullRoleNameTest() {
        User user = createValidUser();
        RoleEntity roleEntity = createRoleEntity();
        roleEntity.setNombre(null);

        when(roleR2dbcRepository.findAll()).thenReturn(Flux.just(roleEntity));

        StepVerifier.create(userRepositoryAdapter.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Role not found"))
                .verify();
    }


    @Test
    void saveUserWithEmptyRoleNameTest() {
        User user = createValidUser();
        RoleEntity roleEntity = createRoleEntity();
        roleEntity.setNombre("");

        when(roleR2dbcRepository.findAll()).thenReturn(Flux.just(roleEntity));

        StepVerifier.create(userRepositoryAdapter.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Role not found"))
                .verify();
    }

    @Test
    void findUserByEmailShouldHandleUserWithNullEmailTest() {
        String email = "nedstark@winterfell.com";
        UserEntity userEntity = createUserEntity();
        userEntity.setEmail(null);

        when(userR2dbcRepository.findAll()).thenReturn(Flux.just(userEntity));

        StepVerifier.create(userRepositoryAdapter.findUserByEmail(email))
                .verifyComplete();
    }

    @Test
    void existsByEmailShouldHandleUserWithNullEmailTest() {
        String email = "nedstark@winterfell.com";
        UserEntity userEntity = createUserEntity();
        userEntity.setEmail(null);

        when(userR2dbcRepository.findAll()).thenReturn(Flux.just(userEntity));

        StepVerifier.create(userRepositoryAdapter.existsByEmail(email))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByEmailShouldHandleRoleWithNullNameInFilterTest() {
        String email = "nedstark@winterfell.com";
        UserEntity userEntity = createUserEntity();
        RoleEntity roleWithNullName = createRoleEntity();
        roleWithNullName.setNombre(null);
        RoleEntity validRole = createRoleEntity();
        validRole.setNombre("CLIENT");

        when(userR2dbcRepository.findAll()).thenReturn(Flux.just(userEntity));
        when(roleR2dbcRepository.findById(anyLong())).thenReturn(Mono.just(validRole));

        StepVerifier.create(userRepositoryAdapter.existsByEmail(email))
                .expectNext(true)
                .verifyComplete();
    }


    @Test
    void saveUserWithMultipleRolesButNoneMatchingTest() {
        User user = createValidUser();
        RoleEntity roleEntity1 = createRoleEntity();
        roleEntity1.setNombre("ADMIN");
        RoleEntity roleEntity2 = createRoleEntity();
        roleEntity2.setNombre("ADVISOR");
        // Línea 52: cuando ningún rol coincide con el buscado

        when(roleR2dbcRepository.findAll()).thenReturn(Flux.just(roleEntity1, roleEntity2));

        StepVerifier.create(userRepositoryAdapter.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Role not found"))
                .verify();
    }


    private User createValidUser() {
        return new User(
                "Ned",
                "Stark",
                "nedstark@winterfell.com",
                "The_NorthRemembers",
                5000000.0,
                LocalDate.of(1990, 1, 1),
                "Winterfell, The North",
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
        entity.setClave("The_NorthRemembers");
        entity.setSalarioBase(5000000.0);
        entity.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        entity.setDireccion("Winterfell, The North");
        entity.setTelefono("123456789");
        entity.setIdRol(2L);
        return entity;
    }

    private RoleEntity createRoleEntity() {
        RoleEntity entity = new RoleEntity();
        entity.setUniqueID(2L);
        entity.setNombre("CLIENT");
        entity.setDescripcion("User with client role");
        return entity;
    }
}