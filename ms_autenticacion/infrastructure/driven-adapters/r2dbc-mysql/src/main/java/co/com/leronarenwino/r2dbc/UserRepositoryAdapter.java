package co.com.leronarenwino.r2dbc;

import co.com.leronarenwino.model.User;
import co.com.leronarenwino.model.gateway.UserRepository;
import co.com.leronarenwino.r2dbc.dto.UserDto;
import co.com.leronarenwino.r2dbc.entity.RoleEntity;
import co.com.leronarenwino.r2dbc.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryAdapter.class);

    private final UserR2dbcRepository userR2dbcRepository;
    private final RoleR2dbcRepository roleR2dbcRepository;

    public UserRepositoryAdapter(UserR2dbcRepository userR2dbcRepository, RoleR2dbcRepository roleR2dbcRepository) {
        this.userR2dbcRepository = userR2dbcRepository;
        this.roleR2dbcRepository = roleR2dbcRepository;
    }

    @Override
    @Transactional
    public Mono<Void> save(User user) {
        log.info("Saving user: {}", user);
        return getRoleIdByName(user.role())
                .map(roleId -> UserDto.toEntity(user, roleId))
                .flatMap(userR2dbcRepository::save)
                .then();
    }

    @Override
    public Mono<User> findUserByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return userR2dbcRepository.findByEmail(email)
                .flatMap(this::mapToUser);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        log.info("Checking existence of user by email: {}", email);
        return userR2dbcRepository.findByEmail(email)
                .hasElement();
    }

    private Mono<Long> getRoleIdByName(String roleName) {
        log.info("Getting roleId by roleName: {}", roleName);
        return roleR2dbcRepository.findAll()
                .filter(role -> roleName.equalsIgnoreCase(role.getNombre()))
                .next()
                .map(RoleEntity::getUniqueID)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found")));
    }

    private Mono<User> mapToUser(UserEntity userEntity) {
        return roleR2dbcRepository.findById(userEntity.getIdRol())
                .filter(role -> role.getNombre() != null)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Role name cannot be null")))
                .map(role -> UserDto.toDomain(userEntity, role.getNombre()))
                .doOnNext(user -> log.info("Found user: {} {}", user.name(), user.lastname()))
                .doOnError(error -> log.error("Error finding user by email: {}", error.getMessage()));
    }
}