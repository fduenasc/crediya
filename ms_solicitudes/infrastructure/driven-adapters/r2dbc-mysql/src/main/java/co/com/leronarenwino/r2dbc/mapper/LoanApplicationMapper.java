package co.com.leronarenwino.r2dbc.mapper;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.r2dbc.entity.SolicitudEntity;

public record LoanApplicationMapper(
) {
    public static LoanApplication toModel(SolicitudEntity entity, String loanTypeName, String loanStatusName) {
        return new LoanApplication(
                entity.getMonto(),
                entity.getPlazo(),
                entity.getDocumentoIdentidad(),
                entity.getEmail(),
                loanTypeName,
                loanStatusName
        );
    }

    public static SolicitudEntity toEntity(LoanApplication loanApplication, Long loanTypeId, Long loanStatusId) {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setMonto(loanApplication.loanAmount());
        entity.setPlazo(loanApplication.termInMonths());
        entity.setDocumentoIdentidad(loanApplication.documentNumber());
        entity.setEmail(loanApplication.email());
        entity.setIdTipoPrestamo(loanTypeId);
        entity.setIdEstado(loanStatusId);
        return entity;
    }
}