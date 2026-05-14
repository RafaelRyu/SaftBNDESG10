package com.example.demo.repository;

import com.example.demo.dto.*;
import com.example.demo.model.OperacaoBNDES;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperacaoBNDESRepository
        extends JpaRepository<OperacaoBNDES, Long>,
                JpaSpecificationExecutor<OperacaoBNDES> {

    // Resumo por UF (já existente)
    @Query("SELECT new com.example.demo.dto.ResumoEstadoDTO(o.uf, SUM(o.valorDaOperacaoEmReais)) " +
           "FROM OperacaoBNDES o GROUP BY o.uf ORDER BY SUM(o.valorDaOperacaoEmReais) DESC")
    List<ResumoEstadoDTO> sumValorPorEstado();

    // Concentração por Agente Financeiro (top 10 por total financiado)
    @Query("SELECT new com.example.demo.dto.AgenteConcentracaoDTO(o.instituicaoFinanceiraCredenciada, COUNT(o), SUM(o.valorDaOperacaoEmReais)) " +
           "FROM OperacaoBNDES o WHERE o.instituicaoFinanceiraCredenciada IS NOT NULL " +
           "GROUP BY o.instituicaoFinanceiraCredenciada ORDER BY SUM(o.valorDaOperacaoEmReais) DESC")
    List<AgenteConcentracaoDTO> concentracaoPorAgente();

    // Distribuição por Porte do Cliente
    @Query("SELECT new com.example.demo.dto.PorteDistribuicaoDTO(o.porteDoCliente, COUNT(o), SUM(o.valorDaOperacaoEmReais)) " +
           "FROM OperacaoBNDES o WHERE o.porteDoCliente IS NOT NULL GROUP BY o.porteDoCliente")
    List<PorteDistribuicaoDTO> distribuicaoPorPorte();

    // Vocações Regionais: cruzamento UF x Subsetor CNAE (top resultados por UF)
    @Query("SELECT new com.example.demo.dto.VocacaoRegionalDTO(o.uf, o.subsetorCnaeNome, COUNT(o), SUM(o.valorDaOperacaoEmReais)) " +
           "FROM OperacaoBNDES o WHERE o.uf IS NOT NULL AND o.subsetorCnaeNome IS NOT NULL " +
           "GROUP BY o.uf, o.subsetorCnaeNome ORDER BY o.uf, SUM(o.valorDaOperacaoEmReais) DESC")
    List<VocacaoRegionalDTO> vocacoesRegionais();

    // Custo do Dinheiro
    @Query("SELECT new com.example.demo.dto.CustoDinheiroDTO(o.custoFinanceiro, COUNT(o), SUM(o.valorDaOperacaoEmReais)) " +
           "FROM OperacaoBNDES o WHERE o.custoFinanceiro IS NOT NULL GROUP BY o.custoFinanceiro")
    List<CustoDinheiroDTO> custoDinheiro();

    // Ticket Médio: filtros dinâmicos (porte, setor, uf, produto)
    @Query("SELECT new com.example.demo.dto.TicketMedioDTO(COUNT(o), COALESCE(SUM(o.valorDaOperacaoEmReais),0), " +
           "CASE WHEN COUNT(o) > 0 THEN SUM(o.valorDaOperacaoEmReais)/COUNT(o) ELSE 0 END) " +
           "FROM OperacaoBNDES o WHERE " +
           "(:porte IS NULL OR o.porteDoCliente = :porte) AND " +
           "(:setorCnae IS NULL OR o.setorCnae LIKE %:setorCnae%) AND " +
           "(:uf IS NULL OR o.uf = :uf) AND " +
           "(:produto IS NULL OR o.produto LIKE %:produto%)")
    TicketMedioDTO ticketMedio(@Param("porte") String porte,
                               @Param("setorCnae") String setorCnae,
                               @Param("uf") String uf,
                               @Param("produto") String produto);

   @Query("SELECT new com.example.demo.dto.SetorBNDESDTO(o.setorBndes, COUNT(o), SUM(o.valorDaOperacaoEmReais)) " +
       "FROM OperacaoBNDES o WHERE o.setorBndes IS NOT NULL " +
       "GROUP BY o.setorBndes ORDER BY SUM(o.valorDaOperacaoEmReais) DESC")
List<SetorBNDESDTO> topSetoresBNDES();

@Query("SELECT new com.example.demo.dto.NaturezaClienteDTO(o.naturezaDoCliente, COUNT(o), SUM(o.valorDaOperacaoEmReais)) " +
       "FROM OperacaoBNDES o WHERE o.naturezaDoCliente IS NOT NULL " +
       "GROUP BY o.naturezaDoCliente")
List<NaturezaClienteDTO> distribuicaoPorNatureza();

@Query("SELECT new com.example.demo.dto.ProdutoDTO(o.produto, COUNT(o), SUM(o.valorDaOperacaoEmReais)) " +
       "FROM OperacaoBNDES o WHERE o.produto IS NOT NULL " +
       "GROUP BY o.produto ORDER BY COUNT(o) DESC")
List<ProdutoDTO> topProdutos();
}