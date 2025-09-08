package co.com.leronarenwino.r2dbc;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.r2dbc.entity.EstadoEntity;
import co.com.leronarenwino.r2dbc.entity.SolicitudEntity;
import co.com.leronarenwino.r2dbc.entity.TipoPrestamoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class LoanApplicationRepositoryAdapterTest {

    private LoanApplicationR2dbcRepository loanApplicationRepository;
    private LoanTypeR2dbcRepository loanTypeRepository;
    private LoanStatusR2dbcRepository loanStatusRepository;
    private LoanApplicationRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = Mockito.mock(LoanApplicationR2dbcRepository.class);
        loanTypeRepository = Mockito.mock(LoanTypeR2dbcRepository.class);
        loanStatusRepository = Mockito.mock(LoanStatusR2dbcRepository.class);
        adapter = new LoanApplicationRepositoryAdapter(
                loanApplicationRepository,
                loanTypeRepository,
                loanStatusRepository
        );
    }

    @Test
    void saveSuccessfullyTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "PERSONAL", "PENDIENTE");

        TipoPrestamoEntity tipoEntity = new TipoPrestamoEntity();
        tipoEntity.setIdTipoPrestamo(1L);
        tipoEntity.setNombre("PERSONAL");

        EstadoEntity estadoEntity = new EstadoEntity();
        estadoEntity.setIdEstado(1L);
        estadoEntity.setNombre("PENDIENTE");

        SolicitudEntity savedEntity = new SolicitudEntity();
        savedEntity.setId(1L);

        Mockito.when(loanTypeRepository.findAll()).thenReturn(Flux.just(tipoEntity));
        Mockito.when(loanStatusRepository.findAll()).thenReturn(Flux.just(estadoEntity));
        Mockito.when(loanApplicationRepository.save(Mockito.any(SolicitudEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        StepVerifier.create(adapter.save(loanApplication))
                .verifyComplete();
    }

    @Test
    void saveLoanTypeNotFoundTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "INEXISTENTE", "PENDIENTE");

        Mockito.when(loanTypeRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.save(loanApplication))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan type not found"))
                .verify();
    }

    @Test
    void saveLoanStatusNotFoundTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "PERSONAL", "INEXISTENTE");

        TipoPrestamoEntity tipoEntity = new TipoPrestamoEntity();
        tipoEntity.setIdTipoPrestamo(1L);
        tipoEntity.setNombre("PERSONAL");

        Mockito.when(loanTypeRepository.findAll()).thenReturn(Flux.just(tipoEntity));
        Mockito.when(loanStatusRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.save(loanApplication))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan status not found"))
                .verify();
    }

    @Test
    void saveCaseInsensitiveMatchTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "personal", "pendiente");

        TipoPrestamoEntity tipoEntity = new TipoPrestamoEntity();
        tipoEntity.setIdTipoPrestamo(1L);
        tipoEntity.setNombre("PERSONAL");

        EstadoEntity estadoEntity = new EstadoEntity();
        estadoEntity.setIdEstado(1L);
        estadoEntity.setNombre("PENDIENTE");

        SolicitudEntity savedEntity = new SolicitudEntity();

        Mockito.when(loanTypeRepository.findAll()).thenReturn(Flux.just(tipoEntity));
        Mockito.when(loanStatusRepository.findAll()).thenReturn(Flux.just(estadoEntity));
        Mockito.when(loanApplicationRepository.save(Mockito.any(SolicitudEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        StepVerifier.create(adapter.save(loanApplication))
                .verifyComplete();
    }

    @Test
    void saveRepositoryErrorTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "PERSONAL", "PENDIENTE");

        TipoPrestamoEntity tipoEntity = new TipoPrestamoEntity();
        tipoEntity.setIdTipoPrestamo(1L);
        tipoEntity.setNombre("PERSONAL");

        EstadoEntity estadoEntity = new EstadoEntity();
        estadoEntity.setIdEstado(1L);
        estadoEntity.setNombre("PENDIENTE");

        Mockito.when(loanTypeRepository.findAll()).thenReturn(Flux.just(tipoEntity));
        Mockito.when(loanStatusRepository.findAll()).thenReturn(Flux.just(estadoEntity));
        Mockito.when(loanApplicationRepository.save(Mockito.any(SolicitudEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.save(loanApplication))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    void saveMultipleLoanTypesFilterCorrectlyTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "PERSONAL", "PENDIENTE");

        TipoPrestamoEntity tipo1 = new TipoPrestamoEntity();
        tipo1.setIdTipoPrestamo(1L);
        tipo1.setNombre("HIPOTECARIO");

        TipoPrestamoEntity tipo2 = new TipoPrestamoEntity();
        tipo2.setIdTipoPrestamo(2L);
        tipo2.setNombre("PERSONAL");

        EstadoEntity estadoEntity = new EstadoEntity();
        estadoEntity.setIdEstado(1L);
        estadoEntity.setNombre("PENDIENTE");

        SolicitudEntity savedEntity = new SolicitudEntity();

        Mockito.when(loanTypeRepository.findAll()).thenReturn(Flux.just(tipo1, tipo2));
        Mockito.when(loanStatusRepository.findAll()).thenReturn(Flux.just(estadoEntity));
        Mockito.when(loanApplicationRepository.save(Mockito.any(SolicitudEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        StepVerifier.create(adapter.save(loanApplication))
                .verifyComplete();
    }

    @Test
    void findAllPaginatedSuccessTest() {
        SolicitudEntity entity1 = new SolicitudEntity();
        entity1.setId(1L);
        entity1.setMonto(1000L);
        entity1.setPlazo(12L);
        entity1.setDocumentoIdentidad(12345678L);
        entity1.setIdTipoPrestamo(1L);
        entity1.setIdEstado(1L);

        SolicitudEntity entity2 = new SolicitudEntity();
        entity2.setId(2L);
        entity2.setMonto(2000L);
        entity2.setPlazo(24L);
        entity2.setDocumentoIdentidad(87654321L);
        entity2.setIdTipoPrestamo(2L);
        entity2.setIdEstado(1L);

        TipoPrestamoEntity tipoPersonal = new TipoPrestamoEntity();
        tipoPersonal.setIdTipoPrestamo(1L);
        tipoPersonal.setNombre("PERSONAL");

        TipoPrestamoEntity tipoHipotecario = new TipoPrestamoEntity();
        tipoHipotecario.setIdTipoPrestamo(2L);
        tipoHipotecario.setNombre("HIPOTECARIO");

        EstadoEntity estadoPendiente = new EstadoEntity();
        estadoPendiente.setIdEstado(1L);
        estadoPendiente.setNombre("PENDIENTE");

        Mockito.when(loanApplicationRepository.findAll())
                .thenReturn(Flux.just(entity1, entity2));
        Mockito.when(loanTypeRepository.findById(1L))
                .thenReturn(Mono.just(tipoPersonal));
        Mockito.when(loanTypeRepository.findById(2L))
                .thenReturn(Mono.just(tipoHipotecario));
        Mockito.when(loanStatusRepository.findById(1L))
                .thenReturn(Mono.just(estadoPendiente));

        StepVerifier.create(adapter.findAllPaginated(0, 10))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findAllPaginatedWithSkipAndTakeTest() {
        SolicitudEntity entity1 = new SolicitudEntity();
        entity1.setIdTipoPrestamo(1L);
        entity1.setIdEstado(1L);

        SolicitudEntity entity2 = new SolicitudEntity();
        entity2.setIdTipoPrestamo(1L);
        entity2.setIdEstado(1L);

        SolicitudEntity entity3 = new SolicitudEntity();
        entity3.setIdTipoPrestamo(1L);
        entity3.setIdEstado(1L);

        TipoPrestamoEntity tipo = new TipoPrestamoEntity();
        tipo.setIdTipoPrestamo(1L);
        tipo.setNombre("PERSONAL");

        EstadoEntity estado = new EstadoEntity();
        estado.setIdEstado(1L);
        estado.setNombre("PENDIENTE");

        Mockito.when(loanApplicationRepository.findAll())
                .thenReturn(Flux.just(entity1, entity2, entity3));
        Mockito.when(loanTypeRepository.findById(1L))
                .thenReturn(Mono.just(tipo));
        Mockito.when(loanStatusRepository.findById(1L))
                .thenReturn(Mono.just(estado));

        StepVerifier.create(adapter.findAllPaginated(1, 1))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllPaginatedEmptyResultTest() {
        Mockito.when(loanApplicationRepository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(adapter.findAllPaginated(0, 10))
                .verifyComplete();
    }

    @Test
    void countSuccessTest() {
        Mockito.when(loanApplicationRepository.count())
                .thenReturn(Mono.just(5L));

        StepVerifier.create(adapter.count())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void countZeroTest() {
        Mockito.when(loanApplicationRepository.count())
                .thenReturn(Mono.just(0L));

        StepVerifier.create(adapter.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void getLoanTypeNameByIdSuccessTest() {
        TipoPrestamoEntity tipo = new TipoPrestamoEntity();
        tipo.setIdTipoPrestamo(1L);
        tipo.setNombre("PERSONAL");

        Mockito.when(loanTypeRepository.findById(1L))
                .thenReturn(Mono.just(tipo));

        SolicitudEntity entity = new SolicitudEntity();
        entity.setIdTipoPrestamo(1L);
        entity.setIdEstado(1L);

        EstadoEntity estado = new EstadoEntity();
        estado.setIdEstado(1L);
        estado.setNombre("PENDIENTE");

        Mockito.when(loanApplicationRepository.findAll())
                .thenReturn(Flux.just(entity));
        Mockito.when(loanStatusRepository.findById(1L))
                .thenReturn(Mono.just(estado));

        StepVerifier.create(adapter.findAllPaginated(0, 1))
                .expectNextMatches(loanApp -> loanApp.loanType().equals("PERSONAL"))
                .verifyComplete();
    }

    @Test
    void getLoanStatusNameByIdSuccessTest() {
        EstadoEntity estado = new EstadoEntity();
        estado.setIdEstado(1L);
        estado.setNombre("APROBADA");

        Mockito.when(loanStatusRepository.findById(1L))
                .thenReturn(Mono.just(estado));

        StepVerifier.create(adapter.getLoanStatusNameById(1L))
                .expectNext("APROBADA")
                .verifyComplete();
    }

    @Test
    void getLoanStatusNameByIdNotFoundTest() {
        Mockito.when(loanStatusRepository.findById(999L))
                .thenReturn(Mono.empty());

        StepVerifier.create(adapter.getLoanStatusNameById(999L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan status not found"))
                .verify();
    }

    @Test
    void findAllPaginatedWithRepositoryErrorTest() {
        Mockito.when(loanApplicationRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Database connection error")));

        StepVerifier.create(adapter.findAllPaginated(0, 10))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Database connection error"))
                .verify();
    }

    @Test
    void countWithRepositoryErrorTest() {
        Mockito.when(loanApplicationRepository.count())
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.count())
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Database error"))
                .verify();
    }
}