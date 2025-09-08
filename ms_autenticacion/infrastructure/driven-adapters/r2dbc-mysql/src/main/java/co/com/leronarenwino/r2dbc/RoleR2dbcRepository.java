package co.com.leronarenwino.r2dbc;

import co.com.leronarenwino.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RoleR2dbcRepository extends ReactiveCrudRepository<RoleEntity, Long> {
}
