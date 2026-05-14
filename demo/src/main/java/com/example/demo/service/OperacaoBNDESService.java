package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.OperacaoBNDES;
import com.example.demo.repository.OperacaoBNDESRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperacaoBNDESService {

    private final OperacaoBNDESRepository repository;
    private final CsvLoaderService csvLoaderService;

    public long carregarDados() {
        return csvLoaderService.carregarCsv();
    }

    // CRUD - CREATE
    @Transactional
    public OperacaoBNDES criar(OperacaoBNDES operacao) {
        if (operacao.getCliente() == null || operacao.getCliente().isBlank()) {
            throw new IllegalArgumentException("O campo 'cliente' é obrigatório.");
        }
        if (operacao.getUf() == null || operacao.getUf().isBlank()) {
            throw new IllegalArgumentException("O campo 'UF' é obrigatório.");
        }
        if (operacao.getValorDaOperacaoEmReais() == null) {
            throw new IllegalArgumentException("O campo 'valor da operação' é obrigatório.");
        }
        return repository.save(operacao);
    }

    // CRUD - UPDATE
    @Transactional
    public OperacaoBNDES atualizar(Long id, OperacaoBNDES operacaoAtualizada) {
        OperacaoBNDES existente = buscarPorId(id); // lança 404 se não encontrado
        existente.setCliente(operacaoAtualizada.getCliente());
        existente.setCpfCnpj(operacaoAtualizada.getCpfCnpj());
        existente.setUf(operacaoAtualizada.getUf());
        existente.setMunicipio(operacaoAtualizada.getMunicipio());
        existente.setMunicipioCodigo(operacaoAtualizada.getMunicipioCodigo());
        existente.setDataDaContratacao(operacaoAtualizada.getDataDaContratacao());
        existente.setValorDaOperacaoEmReais(operacaoAtualizada.getValorDaOperacaoEmReais());
        existente.setValorDesembolsadoReais(operacaoAtualizada.getValorDesembolsadoReais());
        existente.setFonteDeRecursoDesembolsos(operacaoAtualizada.getFonteDeRecursoDesembolsos());
        existente.setCustoFinanceiro(operacaoAtualizada.getCustoFinanceiro());
        existente.setJuros(operacaoAtualizada.getJuros());
        existente.setPrazoCarenciaMeses(operacaoAtualizada.getPrazoCarenciaMeses());
        existente.setPrazoAmortizacaoMeses(operacaoAtualizada.getPrazoAmortizacaoMeses());
        existente.setModalidadeDeApoio(operacaoAtualizada.getModalidadeDeApoio());
        existente.setFormaDeApoio(operacaoAtualizada.getFormaDeApoio());
        existente.setProduto(operacaoAtualizada.getProduto());
        existente.setInstrumentoFinanceiro(operacaoAtualizada.getInstrumentoFinanceiro());
        existente.setInovacao(operacaoAtualizada.getInovacao());
        existente.setAreaOperacional(operacaoAtualizada.getAreaOperacional());
        existente.setSetorCnae(operacaoAtualizada.getSetorCnae());
        existente.setSubsetorCnaeAgrupado(operacaoAtualizada.getSubsetorCnaeAgrupado());
        existente.setSubsetorCnaeCodigo(operacaoAtualizada.getSubsetorCnaeCodigo());
        existente.setSubsetorCnaeNome(operacaoAtualizada.getSubsetorCnaeNome());
        existente.setSetorBndes(operacaoAtualizada.getSetorBndes());
        existente.setSubsetorBndes(operacaoAtualizada.getSubsetorBndes());
        existente.setPorteDoCliente(operacaoAtualizada.getPorteDoCliente());
        existente.setNaturezaDoCliente(operacaoAtualizada.getNaturezaDoCliente());
        existente.setInstituicaoFinanceiraCredenciada(operacaoAtualizada.getInstituicaoFinanceiraCredenciada());
        existente.setCnpjDoAgenteFinanceiro(operacaoAtualizada.getCnpjDoAgenteFinanceiro());
        existente.setSituacaoDaOperacao(operacaoAtualizada.getSituacaoDaOperacao());
        return repository.save(existente);
    }

    // CRUD - DELETE
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Operação não encontrada com id: " + id);
        }
        repository.deleteById(id);
    }

    // Filtros paginados
    public Page<OperacaoBNDES> filtrarPaginado(
            String setor, String uf, Double valorMinimo,
            LocalDateTime dataInicio, LocalDateTime dataFim,
            String situacao, String inovacao,
            String cliente, String porteDoCliente, String naturezaDoCliente,
            String produto, String areaOperacional,
            String modalidadeDeApoio, String formaDeApoio,
            String instrumentoFinanceiro, String custoFinanceiro,
            String fonteDeRecursoDesembolsos,
            Pageable pageable) {
        Specification<OperacaoBNDES> spec = buildSpecification(setor, uf, valorMinimo, dataInicio, dataFim,
                situacao, inovacao, cliente, porteDoCliente, naturezaDoCliente, produto,
                areaOperacional, modalidadeDeApoio, formaDeApoio,
                instrumentoFinanceiro, custoFinanceiro, fonteDeRecursoDesembolsos);
        return repository.findAll(spec, pageable);
    }

    public long contar(
            String setor, String uf, Double valorMinimo,
            LocalDateTime dataInicio, LocalDateTime dataFim,
            String situacao, String inovacao,
            String cliente, String porteDoCliente, String naturezaDoCliente,
            String produto, String areaOperacional,
            String modalidadeDeApoio, String formaDeApoio,
            String instrumentoFinanceiro, String custoFinanceiro,
            String fonteDeRecursoDesembolsos) {
        Specification<OperacaoBNDES> spec = buildSpecification(setor, uf, valorMinimo, dataInicio, dataFim,
                situacao, inovacao, cliente, porteDoCliente, naturezaDoCliente, produto,
                areaOperacional, modalidadeDeApoio, formaDeApoio,
                instrumentoFinanceiro, custoFinanceiro, fonteDeRecursoDesembolsos);
        return repository.count(spec);
    }

    private Specification<OperacaoBNDES> buildSpecification(
            String setor, String uf, Double valorMinimo,
            LocalDateTime dataInicio, LocalDateTime dataFim,
            String situacao, String inovacao,
            String cliente, String porteDoCliente, String naturezaDoCliente,
            String produto, String areaOperacional,
            String modalidadeDeApoio, String formaDeApoio,
            String instrumentoFinanceiro, String custoFinanceiro,
            String fonteDeRecursoDesembolsos) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (setor != null && !setor.isBlank())
                predicates.add(cb.like(cb.lower(root.get("setorBndes")), "%" + setor.toLowerCase() + "%"));
            if (uf != null && !uf.isBlank())
                predicates.add(cb.equal(cb.upper(root.get("uf")), uf.toUpperCase()));
            if (valorMinimo != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("valorDaOperacaoEmReais"), valorMinimo));
            if (dataInicio != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataDaContratacao"), dataInicio));
            if (dataFim != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("dataDaContratacao"), dataFim));
            if (situacao != null && !situacao.isBlank())
                predicates.add(cb.equal(cb.upper(root.get("situacaoDaOperacao")), situacao.toUpperCase()));
            if (inovacao != null && !inovacao.isBlank())
                predicates.add(cb.equal(cb.upper(root.get("inovacao")), inovacao.toUpperCase()));

            if (cliente != null && !cliente.isBlank())
                predicates.add(cb.like(cb.lower(root.get("cliente")), "%" + cliente.toLowerCase() + "%"));
            if (porteDoCliente != null && !porteDoCliente.isBlank())
                predicates.add(cb.equal(cb.upper(root.get("porteDoCliente")), porteDoCliente.toUpperCase()));
            if (naturezaDoCliente != null && !naturezaDoCliente.isBlank())
                predicates.add(cb.equal(cb.upper(root.get("naturezaDoCliente")), naturezaDoCliente.toUpperCase()));
            if (produto != null && !produto.isBlank())
                predicates.add(cb.like(cb.lower(root.get("produto")), "%" + produto.toLowerCase() + "%"));
            if (areaOperacional != null && !areaOperacional.isBlank())
                predicates.add(cb.like(cb.lower(root.get("areaOperacional")), "%" + areaOperacional.toLowerCase() + "%"));
            if (modalidadeDeApoio != null && !modalidadeDeApoio.isBlank())
                predicates.add(cb.equal(cb.upper(root.get("modalidadeDeApoio")), modalidadeDeApoio.toUpperCase()));
            if (formaDeApoio != null && !formaDeApoio.isBlank())
                predicates.add(cb.equal(cb.upper(root.get("formaDeApoio")), formaDeApoio.toUpperCase()));
            if (instrumentoFinanceiro != null && !instrumentoFinanceiro.isBlank())
                predicates.add(cb.like(cb.lower(root.get("instrumentoFinanceiro")), "%" + instrumentoFinanceiro.toLowerCase() + "%"));
            if (custoFinanceiro != null && !custoFinanceiro.isBlank())
                predicates.add(cb.equal(cb.upper(root.get("custoFinanceiro")), custoFinanceiro.toUpperCase()));
            if (fonteDeRecursoDesembolsos != null && !fonteDeRecursoDesembolsos.isBlank())
                predicates.add(cb.like(cb.lower(root.get("fonteDeRecursoDesembolsos")), "%" + fonteDeRecursoDesembolsos.toLowerCase() + "%"));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public OperacaoBNDES buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operação não encontrada com id: " + id));
    }

    public List<ResumoEstadoDTO> resumoPorEstado() {
        return repository.sumValorPorEstado();
    }

    public List<AgenteConcentracaoDTO> concentracaoPorAgente() {
        return repository.concentracaoPorAgente();
    }

    public List<PorteDistribuicaoDTO> distribuicaoPorPorte() {
        return repository.distribuicaoPorPorte();
    }

    public List<VocacaoRegionalDTO> vocacoesRegionais() {
        return repository.vocacoesRegionais();
    }

    public List<CustoDinheiroDTO> custoDinheiro() {
        return repository.custoDinheiro();
    }

    public TicketMedioDTO ticketMedio(String porte, String setorCnae, String uf, String produto) {
        return repository.ticketMedio(porte, setorCnae, uf, produto);
    }

    public List<SetorBNDESDTO> topSetoresBNDES() {
        return repository.topSetoresBNDES();
    }

    public List<NaturezaClienteDTO> distribuicaoPorNatureza() {
        return repository.distribuicaoPorNatureza();
    }

    public List<ProdutoDTO> topProdutos() {
        return repository.topProdutos();
    }
}