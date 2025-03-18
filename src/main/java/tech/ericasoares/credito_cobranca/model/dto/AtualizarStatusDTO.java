package tech.ericasoares.credito_cobranca.model.dto;

import jakarta.validation.constraints.NotNull;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;

public record AtualizarStatusDTO(@NotNull StatusCobranca status) {}
