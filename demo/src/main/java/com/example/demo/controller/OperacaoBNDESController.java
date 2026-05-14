package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.OperacaoBNDES;
import com.example.demo.service.OperacaoBNDESService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OperacaoBNDESController {

    private final OperacaoBNDESService service;

    // Carga manual
    @PostMapping("/carga")
    public ResponseEntity<String> carga() {
        long total = service.carregarDados();
        return ResponseEntity.ok("Carga concluída. Total de registros: " + total);
    }

    // Listagem paginada com filtros
    @GetMapping("/operacoes")
    public ResponseEntity<Page<OperacaoBNDES>> listar(
            @RequestParam(required = false) String setor,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) Double valorMinimo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) String inovacao,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String porteDoCliente,
            @RequestParam(required = false) String naturezaDoCliente,
            @RequestParam(required = false) String produto,
            @RequestParam(required = false) String areaOperacional,
            @RequestParam(required = false) String modalidadeDeApoio,
            @RequestParam(required = false) String formaDeApoio,
            @RequestParam(required = false) String instrumentoFinanceiro,
            @RequestParam(required = false) String custoFinanceiro,
            @RequestParam(required = false) String fonteDeRecursoDesembolsos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Page<OperacaoBNDES> pagina = service.filtrarPaginado(
                setor, uf, valorMinimo, dataInicio, dataFim, situacao, inovacao,
                cliente, porteDoCliente, naturezaDoCliente, produto,
                areaOperacional, modalidadeDeApoio, formaDeApoio,
                instrumentoFinanceiro, custoFinanceiro, fonteDeRecursoDesembolsos,
                PageRequest.of(page, size));
        return ResponseEntity.ok(pagina);
    }

    // Contagem total
    @GetMapping("/operacoes/count")
    public ResponseEntity<Map<String, Long>> contarOperacoes(
            @RequestParam(required = false) String setor,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) Double valorMinimo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) String inovacao,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String porteDoCliente,
            @RequestParam(required = false) String naturezaDoCliente,
            @RequestParam(required = false) String produto,
            @RequestParam(required = false) String areaOperacional,
            @RequestParam(required = false) String modalidadeDeApoio,
            @RequestParam(required = false) String formaDeApoio,
            @RequestParam(required = false) String instrumentoFinanceiro,
            @RequestParam(required = false) String custoFinanceiro,
            @RequestParam(required = false) String fonteDeRecursoDesembolsos) {
        long count = service.contar(
                setor, uf, valorMinimo, dataInicio, dataFim, situacao, inovacao,
                cliente, porteDoCliente, naturezaDoCliente, produto,
                areaOperacional, modalidadeDeApoio, formaDeApoio,
                instrumentoFinanceiro, custoFinanceiro, fonteDeRecursoDesembolsos);
        return ResponseEntity.ok(Map.of("total", count));
    }

    // Detalhes
    @GetMapping("/operacoes/{id}")
    public ResponseEntity<OperacaoBNDES> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // NOVO: Criar operação
    @PostMapping("/operacoes")
    public ResponseEntity<OperacaoBNDES> criar(@RequestBody OperacaoBNDES operacao) {
        OperacaoBNDES nova = service.criar(operacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    // NOVO: Atualizar operação
    @PutMapping("/operacoes/{id}")
    public ResponseEntity<OperacaoBNDES> atualizar(@PathVariable Long id, @RequestBody OperacaoBNDES operacao) {
        OperacaoBNDES atualizada = service.atualizar(id, operacao);
        return ResponseEntity.ok(atualizada);
    }

    // NOVO: Excluir operação
    @DeleteMapping("/operacoes/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Análises agregadas (mantidas)
    @GetMapping("/resumo/estados")
    public ResponseEntity<List<ResumoEstadoDTO>> resumoEstados() {
        return ResponseEntity.ok(service.resumoPorEstado());
    }

    @GetMapping("/resumo/agentes")
    public ResponseEntity<List<AgenteConcentracaoDTO>> resumoAgentes() {
        return ResponseEntity.ok(service.concentracaoPorAgente());
    }

    @GetMapping("/resumo/porte")
    public ResponseEntity<List<PorteDistribuicaoDTO>> distribuicaoPorPorte() {
        return ResponseEntity.ok(service.distribuicaoPorPorte());
    }

    @GetMapping("/resumo/vocacoes")
    public ResponseEntity<List<VocacaoRegionalDTO>> vocacoesRegionais() {
        return ResponseEntity.ok(service.vocacoesRegionais());
    }

    @GetMapping("/resumo/custo")
    public ResponseEntity<List<CustoDinheiroDTO>> custoDinheiro() {
        return ResponseEntity.ok(service.custoDinheiro());
    }

    @GetMapping("/ticket-medio")
    public ResponseEntity<TicketMedioDTO> ticketMedio(
            @RequestParam(required = false) String porte,
            @RequestParam(required = false) String setorCnae,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String produto) {
        return ResponseEntity.ok(service.ticketMedio(porte, setorCnae, uf, produto));
    }

    @GetMapping("/resumo/setores")
    public ResponseEntity<List<SetorBNDESDTO>> resumoSetores() {
        return ResponseEntity.ok(service.topSetoresBNDES());
    }

    @GetMapping("/resumo/natureza")
    public ResponseEntity<List<NaturezaClienteDTO>> resumoNatureza() {
        return ResponseEntity.ok(service.distribuicaoPorNatureza());
    }

    @GetMapping("/resumo/produtos")
    public ResponseEntity<List<ProdutoDTO>> resumoProdutos() {
        return ResponseEntity.ok(service.topProdutos());
    }
}