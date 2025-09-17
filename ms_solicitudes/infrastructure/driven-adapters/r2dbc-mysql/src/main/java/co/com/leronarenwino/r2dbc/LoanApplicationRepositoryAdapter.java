package co.com.leronarenwino.r2dbc;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import co.com.leronarenwino.r2dbc.entity.EstadoEntity;
import co.com.leronarenwino.r2dbc.entity.SolicitudEntity;
import co.com.leronarenwino.r2dbc.entity.TipoPrestamoEntity;
import co.com.leronarenwino.r2dbc.mapper.LoanApplicationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.leronarenwino.r2dbc.mapper.LoanApplicationMapper.toModel;

@Repository
public class LoanApplicationRepositoryAdapter implements LoanApplicationRepository {

    private static final String LOAN_TYPE_NOT_FOUND = "Loan type not found";
    private static final String APPROVED_STATUS = "APROBADA";

    private static final Logger log = LoggerFactory.getLogger(LoanApplicationRepositoryAdapter.class);

    private final LoanApplicationR2dbcRepository loanApplicationR2DbcRepository;
    private final LoanTypeR2dbcRepository loanTypeR2DbcRepository;
    private final LoanStatusR2dbcRepository loanStatusR2DbcRepository;

    public LoanApplicationRepositoryAdapter(LoanApplicationR2dbcRepository loanApplicationR2DbcRepository,
                                            LoanTypeR2dbcRepository loanTypeR2DbcRepository,
                                            LoanStatusR2dbcRepository loanStatusR2DbcRepository) {
        this.loanApplicationR2DbcRepository = loanApplicationR2DbcRepository;
        this.loanTypeR2DbcRepository = loanTypeR2DbcRepository;
        this.loanStatusR2DbcRepository = loanStatusR2DbcRepository;
    }

    @Override
    public Mono<LoanApplication> getLoanApplicationById(Long id) {
        return loanApplicationR2DbcRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Loan application not found")))
                .flatMap(this::mapToLoanApplication)
                .doOnNext(loanApplication -> log.info("Found loan application: {}", loanApplication));
    }

    @Override
    public Mono<Void> saveLoanApplication(LoanApplication loanApplication) {
        log.info("Guardando solicitud: {}", loanApplication);
        return getLoanTypeIdByName(loanApplication.loanType())
                .flatMap(loanTypeId -> getLoanStatusIdByName(loanApplication.loanStatus())
                        .map(loanStatusId -> LoanApplicationMapper.toEntity(loanApplication, loanTypeId, loanStatusId)))
                .flatMap(loanApplicationR2DbcRepository::save)
                .then();
    }

    @Override
    public Mono<Void> updateLoanApplication(Long id, String status) {
        return loanApplicationR2DbcRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Loan application not found")))
                .flatMap(existingLoanApplication ->
                        getLoanStatusIdByName(status)
                                .flatMap(loanStatusId -> {
                                    existingLoanApplication.setIdEstado(loanStatusId);
                                    return loanApplicationR2DbcRepository.save(existingLoanApplication);
                                })
                )
                .then();
    }

    @Override
    public Flux<LoanApplication> findAllApprovedLoansApplicationsByEmail(String email) {
        return loanStatusR2DbcRepository.findByNombre(APPROVED_STATUS)
                .flatMapMany(estado -> loanApplicationR2DbcRepository.findByIdEstadoAndEmail(estado.getIdEstado(), email)
                        .map(entity -> toModel(entity, APPROVED_STATUS, estado.getNombre()))
                );
    }

    @Override
    public Flux<LoanApplication> findAllPaginated(int page, int size) {
        log.info("Getting loan applications with pagination - page: {}, size: {}", page, size);

        long offset = (long) page * size;

        return loanApplicationR2DbcRepository.findAll()
                .skip(offset)
                .take(size)
                .flatMap(this::mapToLoanApplication)
                .doOnNext(loanApplication -> log.info("Found paginated loan application: {}", loanApplication));
    }

    @Override
    public Mono<LoanType> getLoanTypeByName(String loanType) {
        return loanTypeR2DbcRepository.findByNombre(loanType)
                .map(tipoPrestamoEntity -> new LoanType(
                        tipoPrestamoEntity.getMontoMinimo(),
                        tipoPrestamoEntity.getMontoMaximo(),
                        tipoPrestamoEntity.getTasaInteres()
                ))
                .switchIfEmpty(Mono.error(new IllegalArgumentException(LOAN_TYPE_NOT_FOUND)));
    }

    @Override
    public Mono<Long> count() {
        return loanApplicationR2DbcRepository.count();
    }

    @Override
    public Mono<Long> countByStatus(String status) {
        return loanStatusR2DbcRepository.findByNombre(status)
                .flatMap(estadoEntity ->
                        loanApplicationR2DbcRepository.countByIdEstado(estadoEntity.getIdEstado()))
                .switchIfEmpty(Mono.just(0L));
    }

    @Override
    public Mono<Boolean> existsByStatus(String status) {
        return loanStatusR2DbcRepository.findByNombre(status)
                .hasElement()
                .doOnNext(exists -> log.info("Status '{}' exists: {}", status, exists));
    }

    @Override
    public Mono<Boolean> isValidateAutomaticEnableToLoanType(String loanType) {
        return loanTypeR2DbcRepository.findByNombre(loanType)
                .map(TipoPrestamoEntity::getValidacionAutomatica)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(LOAN_TYPE_NOT_FOUND)))
                .doOnNext(calculoAutomatico -> log.info("Loan type '{}' has automatic calculation: {}", loanType, calculoAutomatico));
    }

    private Mono<LoanApplication> mapToLoanApplication(SolicitudEntity solicitudEntity) {
        return Mono.zip(
                Mono.just(solicitudEntity),
                getLoanTypeNameById(solicitudEntity.getIdTipoPrestamo()),
                getLoanStatusNameById(solicitudEntity.getIdEstado())
        ).map(tuple -> toModel(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    private Mono<String> getLoanTypeNameById(Long loanTypeId) {
        return loanTypeR2DbcRepository.findById(loanTypeId)
                .map(TipoPrestamoEntity::getNombre)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(LOAN_TYPE_NOT_FOUND)));
    }

    public Mono<String> getLoanStatusNameById(Long loanStatusId) {
        return loanStatusR2DbcRepository.findById(loanStatusId)
                .map(EstadoEntity::getNombre)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Loan status not found")));
    }

    private Mono<Long> getLoanTypeIdByName(String loanTypeName) {
        log.info("Obteniendo loanTypeId por loanTypeName: {}", loanTypeName);
        return loanTypeR2DbcRepository.findByNombre(loanTypeName)
                .map(TipoPrestamoEntity::getIdTipoPrestamo)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(LOAN_TYPE_NOT_FOUND)));

    }

    private Mono<Long> getLoanStatusIdByName(String loanStatusName) {
        log.info("Obteniendo loanStatusId por loanStatusName: {}", loanStatusName);
        return loanStatusR2DbcRepository.findByNombre(loanStatusName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Loan status not found")))
                .map(EstadoEntity::getIdEstado);

    }
}