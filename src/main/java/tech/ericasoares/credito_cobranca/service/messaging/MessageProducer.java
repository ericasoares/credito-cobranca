package tech.ericasoares.credito_cobranca.service.messaging;

public interface MessageProducer {
    void enviarParaFila(String mensagem);
}
