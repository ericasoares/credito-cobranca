package tech.ericasoares.credito_cobranca.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.ericasoares.credito_cobranca.model.Cobranca;
import tech.ericasoares.credito_cobranca.model.dto.AtualizarStatusDTO;
import tech.ericasoares.credito_cobranca.model.dto.CobrancaDTO;
import tech.ericasoares.credito_cobranca.service.CobrancaService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/cobrancas")
public class CobrancaController {

    @Autowired
    private CobrancaService cobrancaService;


    @PostMapping("/postar-dividas-ativas")
    public ResponseEntity<String> postarDividasAtivas() {
        boolean cobrancasProcessadas = cobrancaService.postarDividasAtivasNaFila();

        if (cobrancasProcessadas) {
            return ResponseEntity.ok("Dívidas ativas postadas na fila com sucesso!");
        } else {
            return ResponseEntity.ok("Nenhuma dívida ativa encontrada para ser postada na fila.");
        }
    }


    @PostMapping("/notificar")
    public String notificarCobranca(@RequestParam Long idCobranca) {
        log.info("Enviando notificação para a cobrança com ID: {}", idCobranca);
        cobrancaService.notificarCobranca(idCobranca);
        return "Notificação enviada com sucesso!";
    }


    @PostMapping
    public Cobranca criarCobranca(@Valid @RequestBody CobrancaDTO cobrancaDTO) {
        log.info("Criando nova cobrança: {}", cobrancaDTO);
        return cobrancaService.criarCobranca(cobrancaDTO);
    }


    @PutMapping("/{idCobranca}/status")
    public Cobranca atualizarStatusCobranca(@PathVariable Long idCobranca, @RequestBody AtualizarStatusDTO statusDTO) {
        log.info("Atualizando status da cobrança com ID: {} para: {}", idCobranca, statusDTO);
        return cobrancaService.atualizarStatusCobranca(idCobranca, statusDTO.status());
    }


    @GetMapping("/dividas-ativas/{idCliente}")
    public List<Cobranca> listarDividasAtivasPorCliente(@PathVariable Long idCliente) {
        log.info("Listando dívidas ativas para o cliente com ID: {}", idCliente);
        return cobrancaService.listarDividasAtivasPorCliente(idCliente);
    }


    @GetMapping("/{idCliente}")
    public List<Cobranca> listarCobrancasPorCliente(@PathVariable Long idCliente) {
        log.info("Listando cobranças para o cliente com ID: {}", idCliente);
        return cobrancaService.listarCobrancasPorCliente(idCliente);
    }


    @PostMapping("/{idCobranca}/negociar")
    public Cobranca iniciarNegociacao(@PathVariable Long idCobranca) {
        log.info("Iniciando negociação para a cobrança com ID: {}", idCobranca);
        return cobrancaService.iniciarNegociacao(idCobranca);
    }
}