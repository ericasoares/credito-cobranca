package tech.ericasoares.credito_cobranca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.ericasoares.credito_cobranca.model.Cobranca;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;

import java.util.List;

public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {
    List<Cobranca> findByIdCliente(Long idCliente);

    @Query("SELECT c FROM Cobranca c WHERE c.status IN :status AND c.enviadoFila = false")
    List<Cobranca> buscarDividasAtivas(@Param("status") List<StatusCobranca> status);

    @Query("SELECT c FROM Cobranca c WHERE c.idCliente = :idCliente AND c.status IN :status AND c.enviadoFila = false")
    List<Cobranca> buscarDividasAtivasPorCliente(@Param("idCliente") Long idCliente, @Param("status") List<StatusCobranca> status);
}