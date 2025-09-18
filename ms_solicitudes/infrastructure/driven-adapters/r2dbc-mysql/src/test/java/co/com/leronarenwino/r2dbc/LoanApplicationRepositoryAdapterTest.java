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
    void countByStatusSuccessTest() {
        EstadoEntity estado = new EstadoEntity();
        estado.setIdEstado(1L);
        Mockito.when(loanStatusRepository.findByNombre("APROBADA")).thenReturn(Mono.just(estado));
        Mockito.when(loanApplicationRepository.countByIdEstado(1L)).thenReturn(Mono.just(3L));

        StepVerifier.create(adapter.countByStatus("APROBADA"))
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countByStatusNotFoundTest() {
        Mockito.when(loanStatusRepository.findByNombre("INEXISTENTE")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.countByStatus("INEXISTENTE"))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void existsByStatusTrueTest() {
        EstadoEntity estado = new EstadoEntity();
        Mockito.when(loanStatusRepository.findByNombre("APROBADA")).thenReturn(Mono.just(estado));

        StepVerifier.create(adapter.existsByStatus("APROBADA"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByStatusFalseTest() {
        Mockito.when(loanStatusRepository.findByNombre("INEXISTENTE")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.existsByStatus("INEXISTENTE"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isValidateAutomaticEnableToLoanTypeSuccessTest() {
        TipoPrestamoEntity tipo = new TipoPrestamoEntity();
        tipo.setValidacionAutomatica(true);
        Mockito.when(loanTypeRepository.findByNombre("PERSONAL")).thenReturn(Mono.just(tipo));

        StepVerifier.create(adapter.isValidateAutomaticEnableToLoanType("PERSONAL"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isValidateAutomaticEnableToLoanTypeNotFoundTest() {
        Mockito.when(loanTypeRepository.findByNombre("INEXISTENTE")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.isValidateAutomaticEnableToLoanType("INEXISTENTE"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan type not found"))
                .verify();
    }

    @Test
    void getLoanTypeByNameSuccessTest() {
        TipoPrestamoEntity tipo = new TipoPrestamoEntity();
        tipo.setMontoMinimo(1000.0);
        tipo.setMontoMaximo(50000.0);
        tipo.setTasaInteres(5.5);
        Mockito.when(loanTypeRepository.findByNombre("PERSONAL")).thenReturn(Mono.just(tipo));

        StepVerifier.create(adapter.getLoanTypeByName("PERSONAL"))
                .expectNextMatches(loanType -> loanType.minAmount() == 1000.0 && loanType.maxAmount() == 50000.0)
                .verifyComplete();
    }

    @Test
    void getLoanTypeByNameNotFoundTest() {
        Mockito.when(loanTypeRepository.findByNombre("INEXISTENTE")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.getLoanTypeByName("INEXISTENTE"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan type not found"))
                .verify();
    }

    @Test
    void getLoanTypeNameByIdNotFoundTest() {
        Mockito.when(loanTypeRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.getLoanTypeNameById(99L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan type not found"))
                .verify();
    }

    @Test
    void getLoanTypeIdByNameSuccessTest() {
        TipoPrestamoEntity tipo = new TipoPrestamoEntity();
        tipo.setIdTipoPrestamo(1L);
        tipo.setNombre("PERSONAL");
        Mockito.when(loanTypeRepository.findByNombre("PERSONAL")).thenReturn(Mono.just(tipo));

        StepVerifier.create(adapter.getLoanTypeIdByName("PERSONAL"))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void getLoanTypeIdByNameNotFoundTest() {
        Mockito.when(loanTypeRepository.findByNombre("INEXISTENTE")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.getLoanTypeIdByName("INEXISTENTE"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan type not found"))
                .verify();
    }

    @Test
    void getLoanStatusIdByNameSuccessTest() {
        EstadoEntity estado = new EstadoEntity();
        estado.setIdEstado(2L);
        estado.setNombre("PENDIENTE");
        Mockito.when(loanStatusRepository.findByNombre("PENDIENTE")).thenReturn(Mono.just(estado));

        StepVerifier.create(adapter.getLoanStatusIdByName("PENDIENTE"))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void getLoanStatusIdByNameNotFoundTest() {
        Mockito.when(loanStatusRepository.findByNombre("INEXISTENTE")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.getLoanStatusIdByName("INEXISTENTE"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan status not found"))
                .verify();
    }

    @Test
    void getLoanApplicationByIdNotFoundTest() {
        Mockito.when(loanApplicationRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.getLoanApplicationById(99L))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan application not found"))
                .verify();
    }


    @Test
    void saveLoanApplicationLoanTypeNotFoundTest() {
        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123L, "test@correo.com", "INEXISTENTE", "PENDIENTE");
        Mockito.when(loanTypeRepository.findByNombre("INEXISTENTE")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.saveLoanApplication(loanApp))
                .expectErrorMatches(IllegalArgumentException.class::isInstance)
                .verify();
    }

    @Test
    void updateLoanApplicationNotFoundTest() {
        Mockito.when(loanApplicationRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateLoanApplication(99L, "APROBADA"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("Loan application not found"))
                .verify();
    }

    @Test
    void updateLoanApplicationStatusNotFoundTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(1L);
        Mockito.when(loanApplicationRepository.findById(1L)).thenReturn(Mono.just(entity));
        Mockito.when(loanStatusRepository.findByNombre("INEXISTENTE")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateLoanApplication(1L, "INEXISTENTE"))
                .expectErrorMatches(IllegalArgumentException.class::isInstance)
                .verify();
    }

    @Test
    void findAllApprovedLoansApplicationsByEmailSuccessTest() {
        EstadoEntity estado = new EstadoEntity();
        estado.setIdEstado(1L);
        estado.setNombre("APROBADA");
        SolicitudEntity entity = new SolicitudEntity();
        entity.setIdEstado(1L);
        entity.setEmail("test@correo.com");

        Mockito.when(loanStatusRepository.findByNombre("APROBADA")).thenReturn(Mono.just(estado));
        Mockito.when(loanApplicationRepository.findByIdEstadoAndEmail(1L, "test@correo.com")).thenReturn(Flux.just(entity));
        Mockito.when(loanTypeRepository.findById(Mockito.anyLong())).thenReturn(Mono.just(new TipoPrestamoEntity()));

        StepVerifier.create(adapter.findAllApprovedLoansApplicationsByEmail("test@correo.com"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllApprovedLoansApplicationsByEmailNoStatusTest() {
        Mockito.when(loanStatusRepository.findByNombre("APROBADA")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findAllApprovedLoansApplicationsByEmail("test@correo.com"))
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