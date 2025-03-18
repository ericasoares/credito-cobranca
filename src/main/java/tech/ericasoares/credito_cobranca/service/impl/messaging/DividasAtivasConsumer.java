package tech.ericasoares.credito_cobranca.service.impl.messaging;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ericasoares.credito_cobranca.service.messaging.MessageConsumer;

@Slf4j
@Service
public class DividasAtivasConsumer {

    private final MessageConsumer messageConsumer;

    @Autowired
    public DividasAtivasConsumer(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @PostConstruct
    public void iniciarConsumer() {
        log.info("Iniciando consumer para a fila.");
        messageConsumer.iniciarConsumo();
    }
}
