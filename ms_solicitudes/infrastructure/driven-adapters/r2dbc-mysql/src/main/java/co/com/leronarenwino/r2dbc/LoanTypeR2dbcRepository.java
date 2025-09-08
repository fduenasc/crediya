package co.com.leronarenwino.r2dbc;

import co.com.leronarenwino.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanTypeR2dbcRepository extends ReactiveCrudRepository<TipoPrestamoEntity, Long> {
    Mono<TipoPrestamoEntity> findByNombre(String nombre);
}
