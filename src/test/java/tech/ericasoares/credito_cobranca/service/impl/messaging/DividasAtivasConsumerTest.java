package tech.ericasoares.credito_cobranca.service.impl.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import tech.ericasoares.credito_cobranca.service.messaging.MessageConsumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DividasAtivasConsumerTest {

    @Mock
    private MessageConsumer messageConsumer;

    @Mock
    private Logger log;

    @InjectMocks
    private DividasAtivasConsumer dividasAtivasConsumer;

    @BeforeEach
    void setUp() {
        dividasAtivasConsumer = new DividasAtivasConsumer(messageConsumer);
    }

    @Test
    void deveIniciarConsumerAoCriarClasse() {
        dividasAtivasConsumer.iniciarConsumer();

        verify(messageConsumer, times(1)).iniciarConsumo();
    }
}