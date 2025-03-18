package tech.ericasoares.credito_cobranca.model.enums;

public enum StatusCobranca {
    PENDENTE("pendente"),
    PAGO("pago"),
    CANCELADO("cancelado"),
    EM_NEGOCIACAO("em_negociacao"),
    ATRASADO("atrasado");

    private final String status;

    StatusCobranca(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.status;
    }

    public static StatusCobranca fromString(String status) {
        for (StatusCobranca statusEnum : StatusCobranca.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + status);
    }
}
