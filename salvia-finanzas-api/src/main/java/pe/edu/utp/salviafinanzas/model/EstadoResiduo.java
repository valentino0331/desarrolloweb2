package pe.edu.utp.salviafinanzas.model;

/**
 * Máquina de estados del ciclo de vida y trazabilidad del RAEE
 */
public enum EstadoResiduo {
    DECLARADO_BAJA,
    ALMACENADO_TEMPORAL,
    SOLICITUD_RECOJO,
    EN_TRANSITO,
    DISPOSICION_FINAL
}
