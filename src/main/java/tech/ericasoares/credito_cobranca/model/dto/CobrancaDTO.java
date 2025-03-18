package tech.ericasoares.credito_cobranca.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;

import java.time.LocalDateTime;

public record CobrancaDTO(
        @NotNull(message = "O ID do cliente é obrigatório")
        Long idCliente,

        @NotNull(message = "O valor da cobrança é obrigatório")
        @Positive(message = "O valor da cobrança deve ser positivo")
        Double valor,

        @NotNull(message = "O status da cobrança é obrigatório")
        StatusCobranca status,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataCobranca
) {}
