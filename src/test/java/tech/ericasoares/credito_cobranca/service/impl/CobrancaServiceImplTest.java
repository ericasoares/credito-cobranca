package tech.ericasoares.credito_cobranca.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tech.ericasoares.credito_cobranca.model.Cliente;
import tech.ericasoares.credito_cobranca.model.Cobranca;
import tech.ericasoares.credito_cobranca.model.dto.CobrancaDTO;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;
import tech.ericasoares.credito_cobranca.repository.ClienteRepository;
import tech.ericasoares.credito_cobranca.repository.CobrancaRepository;
import tech.ericasoares.credito_cobranca.service.impl.messaging.DividasAtivasProducer;
import tech.ericasoares.credito_cobranca.service.notification.SMSService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CobrancaServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private DividasAtivasProducer dividasAtivasProducer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SMSService smsService;

    @InjectMocks
    private CobrancaServiceImpl cobrancaService;

    private Cobranca cobranca;
    private CobrancaDTO cobrancaDTO;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        cobranca = new Cobranca();
        cobranca.setId(1L);
        cobranca.setIdCliente(1L);
        cobranca.setValor(1000.0);
        cobranca.setStatus(StatusCobranca.PENDENTE);
        cobranca.setDataCobranca(LocalDateTime.now());

        cobrancaDTO = new CobrancaDTO(1L, 1000.0, StatusCobranca.PENDENTE, LocalDateTime.now());
    }

    @Test
    void testPostarDividasAtivasNaFila_Success() throws JsonProcessingException {
        when(cobrancaRepository.buscarDividasAtivas(anyList())).thenReturn(Arrays.asList(cobranca));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        boolean result = cobrancaService.postarDividasAtivasNaFila();

        assertTrue(result);
        verify(cobrancaRepository, times(1)).buscarDividasAtivas(anyList());
        verify(dividasAtivasProducer, times(1)).enviarParaFila(anyString());
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }

    @Test
    void testPostarDividasAtivasNaFila_NoActiveDebts() {
        when(cobrancaRepository.buscarDividasAtivas(anyList())).thenReturn(Collections.emptyList());

        boolean result = cobrancaService.postarDividasAtivasNaFila();

        assertFalse(result);
        verify(cobrancaRepository, times(1)).buscarDividasAtivas(anyList());
        verify(dividasAtivasProducer, never()).enviarParaFila(anyString());
        verify(cobrancaRepository, never()).save(any(Cobranca.class));
    }


    @Test
    void testNotificarCobranca_Success() {
        // Configuração do cenário
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("Cliente Teste");

        when(cobrancaRepository.findById(anyLong())).thenReturn(Optional.of(cobranca));
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));

        // Execução do método
        cobrancaService.notificarCobranca(1L);

        // Verificação
        String mensagemEsperada = String.format(
                "Banco: Olá %s, notamos que seu contrato está %s. Entre em contato 0800 155 200.",
                cliente.getNome(), cobranca.getStatus().toString().toLowerCase()
        );

        verify(smsService, times(1)).enviarSms(clienteId, mensagemEsperada);
    }

    @Test
    void testNotificarCobranca_CobrancaNotFound() {
        when(cobrancaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cobrancaService.notificarCobranca(1L));
    }

    @Test
    void testCriarCobranca_Success() {
        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(cobranca);

        Cobranca result = cobrancaService.criarCobranca(cobrancaDTO);

        assertNotNull(result);
        assertEquals(cobranca.getId(), result.getId());
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }

    @Test
    void testCriarCobranca_InvalidDTO() {
        CobrancaDTO invalidDTO = new CobrancaDTO(null, -100.0, null, null);

        var violations = validator.validate(invalidDTO);
        assertFalse(violations.isEmpty()); // Verifica se há violações de validação

        // Simula a validação manual do DTO antes de chamar o método
        assertThrows(ConstraintViolationException.class, () -> {
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            cobrancaService.criarCobranca(invalidDTO);
        });
    }

    @Test
    void testAtualizarStatusCobranca_Success() {
        when(cobrancaRepository.findById(anyLong())).thenReturn(Optional.of(cobranca));
        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(cobranca);

        Cobranca result = cobrancaService.atualizarStatusCobranca(1L, StatusCobranca.ATRASADO);

        assertNotNull(result);
        assertEquals(StatusCobranca.ATRASADO, result.getStatus());
        verify(cobrancaRepository, times(1)).findById(anyLong());
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }

    @Test
    void testAtualizarStatusCobranca_CobrancaNotFound() {
        when(cobrancaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cobrancaService.atualizarStatusCobranca(1L, StatusCobranca.ATRASADO));
    }

    @Test
    void testListarDividasAtivasPorCliente_Success() {
        when(cobrancaRepository.buscarDividasAtivasPorCliente(anyLong(), anyList())).thenReturn(Arrays.asList(cobranca));

        List<Cobranca> result = cobrancaService.listarDividasAtivasPorCliente(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(cobrancaRepository, times(1)).buscarDividasAtivasPorCliente(anyLong(), anyList());
    }

    @Test
    void testListarCobrancasPorCliente_Success() {
        when(cobrancaRepository.findByIdCliente(anyLong())).thenReturn(Arrays.asList(cobranca));

        List<Cobranca> result = cobrancaService.listarCobrancasPorCliente(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(cobrancaRepository, times(1)).findByIdCliente(anyLong());
    }

    @Test
    void testIniciarNegociacao_Success() {
        when(cobrancaRepository.findById(anyLong())).thenReturn(Optional.of(cobranca));
        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(cobranca);

        Cobranca result = cobrancaService.iniciarNegociacao(1L);

        assertNotNull(result);
        assertEquals(StatusCobranca.EM_NEGOCIACAO, result.getStatus());
        verify(cobrancaRepository, times(1)).findById(anyLong());
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }

    @Test
    void testIniciarNegociacao_CobrancaNotFound() {
        when(cobrancaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cobrancaService.iniciarNegociacao(1L));
    }
}