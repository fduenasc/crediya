package co.com.leronarenwino.model;

public record ApprovedEvent(
        String eventType
) {
    public static final String SOLICITUD_APROBADA = "SOLICITUD_APROBADA";

    public boolean isSolicitudAprobada() {
        return SOLICITUD_APROBADA.equals(eventType);
    }
}
