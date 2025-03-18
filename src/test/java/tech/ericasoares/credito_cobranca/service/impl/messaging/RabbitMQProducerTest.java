package tech.ericasoares.credito_cobranca.service.impl.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RabbitMQProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQProducer rabbitMQProducer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEnviarParaFila_Sucesso() {
        String mensagem = "Teste de mensagem";

        rabbitMQProducer.enviarParaFila(mensagem);

        verify(rabbitTemplate, times(1)).convertAndSend("dividas_ativas", mensagem);
    }
}
