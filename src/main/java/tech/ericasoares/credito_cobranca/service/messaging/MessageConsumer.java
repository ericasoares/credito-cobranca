package tech.ericasoares.credito_cobranca.service.messaging;

public interface MessageConsumer {
    void iniciarConsumo();
    void processarMensagem(String mensagem);
}
