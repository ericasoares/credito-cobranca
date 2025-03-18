package tech.ericasoares.credito_cobranca.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;

import java.time.LocalDateTime;

@Data
@Entity
public class Cobranca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idCliente")
    private Long idCliente;

    private Double valor;

    @Enumerated(EnumType.STRING)
    private StatusCobranca status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "dataCobranca")
    private LocalDateTime dataCobranca;

    @Column(name = "enviadoFila")
    private boolean enviadoFila = false;
}