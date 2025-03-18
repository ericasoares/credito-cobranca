package tech.ericasoares.credito_cobranca.service.impl.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ericasoares.credito_cobranca.service.messaging.MessageProducer;

@Service
public class DividasAtivasProducer {

    private final MessageProducer messageProducer;

    @Autowired
    public DividasAtivasProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    public void enviarParaFila(String mensagem) {
        messageProducer.enviarParaFila(mensagem);
    }
}
