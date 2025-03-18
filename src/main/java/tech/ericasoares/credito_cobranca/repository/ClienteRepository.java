package tech.ericasoares.credito_cobranca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.ericasoares.credito_cobranca.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}