package tech.ericasoares.credito_cobranca.service.impl.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tech.ericasoares.credito_cobranca.model.Cobranca;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;
import tech.ericasoares.credito_cobranca.service.CobrancaService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

class RabbitMQConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CobrancaService cobrancaService;

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @InjectMocks
    private RabbitMQConsumer rabbitMQConsumer;

    private Cobranca cobranca;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        cobranca = new Cobranca();
        cobranca.setId(1L);
        cobranca.setValor(100.0);
        cobranca.setStatus(StatusCobranca.PENDENTE);

        // Configuração do mock de conexão e canal
        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    @Test
    void testProcessarMensagem_Sucesso() throws Exception {
        String mensagem = "{\"id\":1,\"valor\":100.0,\"status\":\"PENDENTE\"}";
        when(objectMapper.readValue(mensagem, Cobranca.class)).thenReturn(cobranca);

        rabbitMQConsumer.processarMensagem(mensagem);

        verify(cobrancaService, times(1)).notificarCobranca(1L);
    }

    @Test
    void testProcessarMensagem_FalhaNaConversao() throws Exception {
        String mensagem = "{\"id\":\"invalid\",\"valor\":100.0,\"status\":\"PENDENTE\"}";
        when(objectMapper.readValue(mensagem, Cobranca.class)).thenThrow(new RuntimeException("Erro na conversão"));

        rabbitMQConsumer.processarMensagem(mensagem);

        verify(cobrancaService, times(0)).notificarCobranca(anyLong());
    }
}