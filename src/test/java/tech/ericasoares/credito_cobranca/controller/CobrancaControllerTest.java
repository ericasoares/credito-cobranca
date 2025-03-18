package tech.ericasoares.credito_cobranca.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tech.ericasoares.credito_cobranca.model.Cobranca;
import tech.ericasoares.credito_cobranca.model.dto.AtualizarStatusDTO;
import tech.ericasoares.credito_cobranca.model.dto.CobrancaDTO;
import tech.ericasoares.credito_cobranca.model.enums.StatusCobranca;
import tech.ericasoares.credito_cobranca.service.CobrancaService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaControllerTest {

    @Mock
    private CobrancaService cobrancaService;

    @InjectMocks
    private CobrancaController cobrancaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostarDividasAtivas_ComDividas() {
        when(cobrancaService.postarDividasAtivasNaFila()).thenReturn(true);

        ResponseEntity<String> response = cobrancaController.postarDividasAtivas();

        assertEquals("Dívidas ativas postadas na fila com sucesso!", response.getBody());
        verify(cobrancaService, times(1)).postarDividasAtivasNaFila();
    }

    @Test
    void testPostarDividasAtivas_SemDividas() {
        when(cobrancaService.postarDividasAtivasNaFila()).thenReturn(false);

        ResponseEntity<String> response = cobrancaController.postarDividasAtivas();

        assertEquals("Nenhuma dívida ativa encontrada para ser postada na fila.", response.getBody());
        verify(cobrancaService, times(1)).postarDividasAtivasNaFila();
    }

    @Test
    void testNotificarCobranca() {
        Long idCobranca = 1L;

        String response = cobrancaController.notificarCobranca(idCobranca);

        assertEquals("Notificação enviada com sucesso!", response);
        verify(cobrancaService, times(1)).notificarCobranca(idCobranca);
    }

    @Test
    void testCriarCobranca() {
        CobrancaDTO cobrancaDTO = new CobrancaDTO(1L, 100.0, StatusCobranca.PENDENTE, LocalDateTime.now());

        Cobranca cobrancaMock = new Cobranca();
        when(cobrancaService.criarCobranca(any(CobrancaDTO.class))).thenReturn(cobrancaMock);

        Cobranca response = cobrancaController.criarCobranca(cobrancaDTO);

        assertNotNull(response);
        verify(cobrancaService, times(1)).criarCobranca(any(CobrancaDTO.class));
    }

    @Test
    void testAtualizarStatusCobranca() {
        Long idCobranca = 1L;
        AtualizarStatusDTO statusDTO = new AtualizarStatusDTO(StatusCobranca.PAGO);
        Cobranca cobrancaMock = new Cobranca();
        when(cobrancaService.atualizarStatusCobranca(idCobranca, statusDTO.status())).thenReturn(cobrancaMock);

        Cobranca response = cobrancaController.atualizarStatusCobranca(idCobranca, statusDTO);

        assertNotNull(response);
        verify(cobrancaService, times(1)).atualizarStatusCobranca(idCobranca, statusDTO.status());
    }

    @Test
    void testListarDividasAtivasPorCliente() {
        Long idCliente = 1L;
        List<Cobranca> cobrancasMock = Arrays.asList(new Cobranca(), new Cobranca());
        when(cobrancaService.listarDividasAtivasPorCliente(idCliente)).thenReturn(cobrancasMock);

        List<Cobranca> response = cobrancaController.listarDividasAtivasPorCliente(idCliente);

        assertEquals(2, response.size());
        verify(cobrancaService, times(1)).listarDividasAtivasPorCliente(idCliente);
    }

    @Test
    void testListarCobrancasPorCliente() {
        Long idCliente = 1L;
        List<Cobranca> cobrancasMock = Arrays.asList(new Cobranca(), new Cobranca());
        when(cobrancaService.listarCobrancasPorCliente(idCliente)).thenReturn(cobrancasMock);

        List<Cobranca> response = cobrancaController.listarCobrancasPorCliente(idCliente);

        assertEquals(2, response.size());
        verify(cobrancaService, times(1)).listarCobrancasPorCliente(idCliente);
    }

    @Test
    void testIniciarNegociacao() {
        Long idCobranca = 1L;
        Cobranca cobrancaMock = new Cobranca();
        when(cobrancaService.iniciarNegociacao(idCobranca)).thenReturn(cobrancaMock);

        Cobranca response = cobrancaController.iniciarNegociacao(idCobranca);

        assertNotNull(response);
        verify(cobrancaService, times(1)).iniciarNegociacao(idCobranca);
    }
}
