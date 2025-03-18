package tech.ericasoares.credito_cobranca.service.impl.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.ericasoares.credito_cobranca.service.messaging.MessageProducer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DividasAtivasProducerTest {

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private DividasAtivasProducer dividasAtivasProducer;

    @Test
    public void testEnviarParaFila() {
        String mensagem = "Mensagem de teste";

        assertDoesNotThrow(() -> dividasAtivasProducer.enviarParaFila(mensagem));

        verify(messageProducer, times(1)).enviarParaFila(mensagem);
    }
}
