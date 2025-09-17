package co.com.leronarenwino.r2dbc;


import co.com.leronarenwino.r2dbc.entity.SolicitudEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationR2dbcRepository extends ReactiveCrudRepository<SolicitudEntity, Long> {
    Mono<Long> countByIdEstado(Long idEstado);
    Flux<SolicitudEntity> findByIdEstadoAndEmail(Long idEstado, String email);
}