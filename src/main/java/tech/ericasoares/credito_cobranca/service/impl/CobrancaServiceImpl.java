package tech.ericasoares.credito_cobranca.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.exception.TwilioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.ericasoares.credito_cobranca.exception.CobrancaSerializationException;
import tech.ericasoares.credito_cobranca.model.Cliente;
import tech.ericasoares.credito_cobranca.model.Cobranca;
import tech.ericasoares.credito_cobranca.model.MensagemNaoEnviada;
import tech.ericasoares.credito_cobranca.model.dto.CobrancaDTO;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;
import tech.ericasoares.credito_cobranca.repository.ClienteRepository;
import tech.ericasoares.credito_cobranca.repository.CobrancaRepository;
import tech.ericasoares.credito_cobranca.repository.MensagemNaoEnviadaRepository;
import tech.ericasoares.credito_cobranca.service.CobrancaService;
import tech.ericasoares.credito_cobranca.service.impl.messaging.DividasAtivasProducer;
import tech.ericasoares.credito_cobranca.service.notification.SMSService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CobrancaServiceImpl implements CobrancaService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CobrancaRepository cobrancaRepository;

    @Autowired
    private DividasAtivasProducer dividasAtivasProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private SMSService smsService;

    @Autowired
    private MensagemNaoEnviadaRepository mensagemNaoEnviadaRepository;


    @Transactional
    @Override
    public boolean postarDividasAtivasNaFila() {
        try {
            List<StatusCobranca> statusEnums = List.of(StatusCobranca.PENDENTE, StatusCobranca.ATRASADO);

            log.info("Buscando cobranças com status: " + statusEnums.stream()
                    .map(StatusCobranca::getStatus)
                    .collect(Collectors.joining(", ")));

            List<Cobranca> dividasAtivas = cobrancaRepository.buscarDividasAtivas(statusEnums);

            if (dividasAtivas != null && !dividasAtivas.isEmpty()) {
                log.info("Encontradas {} cobranças ativas para processar.", dividasAtivas.size());

                for (Cobranca cobranca : dividasAtivas) {
                    try {
                        String mensagem = converterCobrancaParaMensagem(cobranca);
                        log.debug("Cobrança serializada: {}", mensagem);

                        dividasAtivasProducer.enviarParaFila(mensagem);

                        cobranca.setEnviadoFila(true);
                        cobrancaRepository.save(cobranca);
                        log.info("Cobrança ID {} enviada para a fila e marcada como processada.", cobranca.getId());
                    } catch (Exception e) {
                        log.error("Erro ao processar cobrança ID {}: {}", cobranca.getId(), e.getMessage(), e);
                    }
                }
                return true;
            } else {
                log.info("Nenhuma dívida ativa encontrada para ser postada na fila.");
                return false;
            }
        } catch (IllegalArgumentException e) {
            log.error("Erro ao converter status: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Erro inesperado ao processar cobranças: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void notificarCobranca(Long idCobranca) {
        Cobranca cobranca = cobrancaRepository.findById(idCobranca).orElseThrow(() -> {
            log.error("Cobrança com ID {} não encontrada", idCobranca);
            return new RuntimeException("Cobrança não encontrada");
        });

        Cliente cliente = clienteRepository.findById(cobranca.getIdCliente()).orElseThrow(() -> {
            log.error("Cliente com ID {} não encontrado", cobranca.getIdCliente());
            return new RuntimeException("Cliente não encontrado");
        });

        String status = cobranca.getStatus().toString().toLowerCase();

        String mensagem = String.format(
                "Banco: Olá %s, notamos que seu contrato está %s. Entre em contato 0800 155 200.",
                cliente.getNome(), status
        );


        try {
            smsService.enviarSms(cliente.getId(), mensagem);
            log.info("Notificação enviada para a cobrança com ID: {}", idCobranca);
        } catch (TwilioException e) {
            log.error("Falha ao enviar SMS para cobrança {}: {}", idCobranca, e.getMessage());
            salvarMensagemParaReenvio(cliente.getId(), mensagem);
        }
    }


     private String converterCobrancaParaMensagem(Cobranca cobranca) {
        try {
            return objectMapper.writeValueAsString(cobranca);
        } catch (Exception e) {
            log.error("Erro ao serializar cobrança ID {}: {}", cobranca.getId(), e.getMessage(), e);
            throw new CobrancaSerializationException("Erro ao converter cobrança para mensagem", e);
        }
    }


    private void salvarMensagemParaReenvio(Long idCliente, String mensagem) {
        MensagemNaoEnviada mensagemNaoEnviada = new MensagemNaoEnviada(idCliente, mensagem, LocalDateTime.now());
        mensagemNaoEnviadaRepository.save(mensagemNaoEnviada);
        log.info("Mensagem salva para reenvio futuro: {}", mensagem);
    }


    @Override
    public Cobranca criarCobranca(CobrancaDTO cobrancaDTO) {
        log.debug("Criando nova cobrança: {}", cobrancaDTO);
        Cobranca cobranca = new Cobranca();
        cobranca.setIdCliente(cobrancaDTO.idCliente());
        cobranca.setValor(cobrancaDTO.valor());
        cobranca.setStatus(cobrancaDTO.status());
        cobranca.setDataCobranca(LocalDateTime.now());
        Cobranca cobrancaSalva = cobrancaRepository.save(cobranca);
        log.info("Cobrança criada com sucesso: {}", cobrancaSalva);
        return cobrancaSalva;
    }


    @Override
    public Cobranca atualizarStatusCobranca(Long idCobranca, StatusCobranca status) {
        log.debug("Atualizando status da cobrança com ID: {} para: {}", idCobranca, status);
        Cobranca cobranca = cobrancaRepository.findById(idCobranca).orElseThrow(() -> {
            log.error("Cobrança com ID {} não encontrada", idCobranca);
            return new RuntimeException("Cobrança não encontrada");
        });
        cobranca.setStatus(status);
        return cobrancaRepository.save(cobranca);
    }


    @Override
    public List<Cobranca> listarDividasAtivasPorCliente(Long idCliente) {
        return cobrancaRepository.buscarDividasAtivasPorCliente(idCliente,
                List.of(StatusCobranca.PENDENTE, StatusCobranca.ATRASADO));
    }


    @Override
    public List<Cobranca> listarCobrancasPorCliente(Long idCliente) {
        log.debug("Buscando cobranças para o cliente com ID: {}", idCliente);
        return cobrancaRepository.findByIdCliente(idCliente);
    }


    @Override
    public Cobranca iniciarNegociacao(Long idCobranca) {
        log.debug("Iniciando negociação para a cobrança com ID: {}", idCobranca);
        Cobranca cobranca = cobrancaRepository.findById(idCobranca).orElseThrow(() -> {
            log.error("Cobrança com ID {} não encontrada", idCobranca);
            return new RuntimeException("Cobrança não encontrada");
        });
        cobranca.setStatus(StatusCobranca.EM_NEGOCIACAO);
        return cobrancaRepository.save(cobranca);
    }
}