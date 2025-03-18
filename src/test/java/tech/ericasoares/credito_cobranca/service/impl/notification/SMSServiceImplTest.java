package tech.ericasoares.credito_cobranca.service.impl.notification;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import tech.ericasoares.credito_cobranca.config.TwilioConfig;
import tech.ericasoares.credito_cobranca.model.Cliente;
import tech.ericasoares.credito_cobranca.repository.ClienteRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SMSServiceImplTest {

    @Mock
    private TwilioConfig twilioConfig;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private SMSServiceImpl smsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initializes the mocks and injects them
    }


    @Test
    void testEnviarSms_Sucesso() {
        Long clienteId = 1L;
        String mensagem = "Teste de SMS";
        String telefoneCliente = "999999999";

        Cliente cliente = new Cliente();
        cliente.setTelefone(telefoneCliente);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(twilioConfig.getFromPhoneNumber()).thenReturn("1234567890");


        try (MockedStatic<Message> mockedMessage = mockStatic(Message.class)) {
            MessageCreator messageCreatorMock = mock(MessageCreator.class);
            mockedMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(messageCreatorMock);

            // Simular a criação de uma mensagem
            when(messageCreatorMock.create()).thenReturn(mock(Message.class));

            smsService.enviarSms(clienteId, mensagem);

            verify(clienteRepository, times(1)).findById(clienteId);
            verify(messageCreatorMock, times(1)).create();
        }
    }

    @Test
    public void testEnviarSms_ClienteNaoEncontrado() {
        Long clienteId = 999L;  // Non-existent client ID
        String mensagem = "Teste de mensagem SMS";

        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> smsService.enviarSms(clienteId, mensagem));
        assertEquals("Cliente não encontrado", thrown.getMessage());
    }
}