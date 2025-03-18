package tech.ericasoares.credito_cobranca.service.impl.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tech.ericasoares.credito_cobranca.model.MensagemNaoEnviada;
import tech.ericasoares.credito_cobranca.repository.MensagemNaoEnviadaRepository;
import tech.ericasoares.credito_cobranca.service.notification.SMSService;

import java.util.List;

@Slf4j
@Service
public class ReenvioMensagemService {

    private final MensagemNaoEnviadaRepository mensagemNaoEnviadaRepository;
    private final SMSService smsService;

    public ReenvioMensagemService(MensagemNaoEnviadaRepository mensagemNaoEnviadaRepository, SMSService smsService) {
        this.mensagemNaoEnviadaRepository = mensagemNaoEnviadaRepository;
        this.smsService = smsService;
    }

    @Scheduled(fixedDelay = 60000) // Tenta reenviar a cada 60 segundos
    public void reenviarMensagensPendentes() {
        List<MensagemNaoEnviada> mensagensPendentes = mensagemNaoEnviadaRepository.findByReenviadoFalse();

        for (MensagemNaoEnviada mensagem : mensagensPendentes) {
            try {
                smsService.enviarSms(mensagem.getIdCliente(), mensagem.getMensagem());
                mensagem.setReenviado(true);
                mensagemNaoEnviadaRepository.save(mensagem);
                log.info("Mensagem reenviada com sucesso: {}", mensagem);
            } catch (Exception e) {
                log.error("Falha ao reenviar mensagem para o cliente {}: {}", mensagem.getIdCliente(), e.getMessage());
            }
        }
    }
}
