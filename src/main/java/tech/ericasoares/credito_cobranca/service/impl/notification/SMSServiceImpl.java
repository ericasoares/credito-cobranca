package tech.ericasoares.credito_cobranca.service.impl.notification;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ericasoares.credito_cobranca.config.TwilioConfig;
import tech.ericasoares.credito_cobranca.model.Cliente;
import tech.ericasoares.credito_cobranca.repository.ClienteRepository;
import tech.ericasoares.credito_cobranca.service.notification.SMSService;

@Slf4j
@Service
public class SMSServiceImpl implements SMSService {

    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public void enviarSms(Long clienteId, String mensagem) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        String fromPhoneNumber = twilioConfig.getFromPhoneNumber();
        String numeroDestinatario = "+" + cliente.getTelefone();

        Message.creator(
                new PhoneNumber(numeroDestinatario),
                new PhoneNumber(fromPhoneNumber),
                mensagem
        ).create();
    }
}
