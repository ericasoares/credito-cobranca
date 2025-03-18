package tech.ericasoares.credito_cobranca.service.impl.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tech.ericasoares.credito_cobranca.service.messaging.MessageProducer;

@Service
@Primary
public class RabbitMQProducer implements MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void enviarParaFila(String mensagem) {
        rabbitTemplate.convertAndSend("dividas_ativas", mensagem);
    }
}
