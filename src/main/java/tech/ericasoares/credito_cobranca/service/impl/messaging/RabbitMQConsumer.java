package tech.ericasoares.credito_cobranca.service.impl.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ericasoares.credito_cobranca.model.Cobranca;
import tech.ericasoares.credito_cobranca.service.CobrancaService;
import tech.ericasoares.credito_cobranca.service.messaging.MessageConsumer;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class RabbitMQConsumer implements MessageConsumer {

    private static final String FILA_DIVIDAS_ATIVAS = "dividas_ativas";

    private final ObjectMapper objectMapper;
    private final CobrancaService cobrancaService;

    @Autowired
    public RabbitMQConsumer(ObjectMapper objectMapper, CobrancaService cobrancaService) {
        this.objectMapper = objectMapper;
        this.cobrancaService = cobrancaService;
    }

    @Override
    @PostConstruct
    public void iniciarConsumo() {
        log.info("Iniciando consumer para a fila '{}'", FILA_DIVIDAS_ATIVAS);
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("user");
            factory.setPassword("password");
            factory.setVirtualHost("/novo_vhost");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(FILA_DIVIDAS_ATIVAS, false, false, false, null);

            log.info("Aguardando mensagens na fila '{}'. Para sair, pressione CTRL+C", FILA_DIVIDAS_ATIVAS);

            // Callback para processar as mensagens
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String mensagem = new String(delivery.getBody(), StandardCharsets.UTF_8);
                log.info("Mensagem recebida: {}", mensagem);

                processarMensagem(mensagem);
            };

            // Consome as mensagens da fila
            channel.basicConsume(FILA_DIVIDAS_ATIVAS, true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            log.error("Erro ao iniciar o consumer: {}", e.getMessage(), e);
        }
    }

    @Override
    public void processarMensagem(String mensagem) {
        log.info("Processando mensagem: {}", mensagem);

        // Converter a mensagem de volta para um objeto Cobranca
        try {
            Cobranca cobranca = objectMapper.readValue(mensagem, Cobranca.class);
            log.info("Cobrança processada: ID {}, Valor {}, Status {}",
                    cobranca.getId(), cobranca.getValor(), cobranca.getStatus());

            // Notifica o cliente
            cobrancaService.notificarCobranca(cobranca.getId());
        } catch (Exception e) {
            log.error("Erro ao processar mensagem: {}", e.getMessage(), e);
        }
    }
}
