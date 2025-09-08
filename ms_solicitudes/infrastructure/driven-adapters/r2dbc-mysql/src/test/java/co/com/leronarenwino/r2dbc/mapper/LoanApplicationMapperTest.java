package co.com.leronarenwino.r2dbc.mapper;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.r2dbc.entity.SolicitudEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationMapperTest {

    @Test
    void mapperInstantiationTest() {
        LoanApplicationMapper mapper = new LoanApplicationMapper();
        assertNotNull(mapper);
    }

    @Test
    void toEntityMapsAllFieldsCorrectlyTest() {
        LoanApplication loanApplication = new LoanApplication(
                15000L,
                24L,
                12345678L,
                "nedstark@winterfell.wo",
                "PERSONAL",
                "PENDIENTE"
        );
        Long loanTypeId = 1L;
        Long loanStatusId = 2L;

        SolicitudEntity entity = LoanApplicationMapper.toEntity(loanApplication, loanTypeId, loanStatusId);

        assertEquals(15000L, entity.getMonto());
        assertEquals(24L, entity.getPlazo());
        assertEquals(12345678L, entity.getDocumentoIdentidad());
        assertEquals(1L, entity.getIdTipoPrestamo());
        assertEquals(2L, entity.getIdEstado());
        assertNull(entity.getId());
    }

    @Test
    void toEntityWithNullValuesTest() {
        LoanApplication loanApplication = new LoanApplication(
                null,
                null,
                null,
                "nedstark@winterfell.wo",
                "PERSONAL",
                "PENDIENTE"
        );

        SolicitudEntity entity = LoanApplicationMapper.toEntity(loanApplication, null, null);

        assertNull(entity.getMonto());
        assertNull(entity.getPlazo());
        assertNull(entity.getDocumentoIdentidad());
        assertNull(entity.getIdTipoPrestamo());
        assertNull(entity.getIdEstado());
        assertNull(entity.getId());
    }

    @Test
    void toEntityWithBoundaryValuesTest() {
        LoanApplication loanApplication = new LoanApplication(
                0L,
                1L,
                999999999L,
                "nedstark@winterfell.wo",
                "HIPOTECARIO",
                "APROBADA"
        );
        Long loanTypeId = Long.MAX_VALUE;
        Long loanStatusId = Long.MIN_VALUE;

        SolicitudEntity entity = LoanApplicationMapper.toEntity(loanApplication, loanTypeId, loanStatusId);

        assertEquals(0L, entity.getMonto());
        assertEquals(1L, entity.getPlazo());
        assertEquals(999999999L, entity.getDocumentoIdentidad());
        assertEquals(Long.MAX_VALUE, entity.getIdTipoPrestamo());
        assertEquals(Long.MIN_VALUE, entity.getIdEstado());
    }
}