package tech.ericasoares.credito_cobranca.service;

import tech.ericasoares.credito_cobranca.model.Cobranca;
import tech.ericasoares.credito_cobranca.model.dto.CobrancaDTO;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;

import java.util.List;

public interface CobrancaService {

    boolean postarDividasAtivasNaFila();
    void notificarCobranca(Long idCobranca);

    Cobranca criarCobranca(CobrancaDTO cobrancaDTO);
    Cobranca atualizarStatusCobranca(Long idCobranca, StatusCobranca status);
    List<Cobranca> listarCobrancasPorCliente(Long idCliente);
    Cobranca iniciarNegociacao(Long idCobranca);
    List<Cobranca> listarDividasAtivasPorCliente(Long idCliente);
}
