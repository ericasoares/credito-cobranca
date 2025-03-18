package tech.ericasoares.credito_cobranca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.ericasoares.credito_cobranca.model.MensagemNaoEnviada;

import java.util.List;

public interface MensagemNaoEnviadaRepository extends JpaRepository<MensagemNaoEnviada, Long> {
    List<MensagemNaoEnviada> findByReenviadoFalse();
}
