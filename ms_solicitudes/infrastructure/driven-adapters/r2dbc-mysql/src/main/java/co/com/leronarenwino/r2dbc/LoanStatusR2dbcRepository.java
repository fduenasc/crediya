package co.com.leronarenwino.r2dbc;

import co.com.leronarenwino.r2dbc.entity.EstadoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanStatusR2dbcRepository extends ReactiveCrudRepository<EstadoEntity, Long> {
    Mono<EstadoEntity> findByNombre(String nombre);
}
