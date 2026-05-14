package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operacoes_bndes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoBNDES {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInterno; // ID gerado pelo banco

    @Column(length = 500)
    private String cliente;

    private String cpfCnpj;
    private String uf;
    private String municipio;
    private String municipioCodigo;

    private LocalDateTime dataDaContratacao;

    private Double valorDaOperacaoEmReais;
    private Double valorDesembolsadoReais;

    @Column(length = 1000)
    private String fonteDeRecursoDesembolsos;

    private String custoFinanceiro;
    private Double juros;

    private Integer prazoCarenciaMeses;
    private Integer prazoAmortizacaoMeses;

    private String modalidadeDeApoio;
    private String formaDeApoio;
    private String produto;
    private String instrumentoFinanceiro;
    private String inovacao;

    private String areaOperacional;
    private String setorCnae;
    private String subsetorCnaeAgrupado;
    private String subsetorCnaeCodigo;
    private String subsetorCnaeNome;
    private String setorBndes;
    private String subsetorBndes;

    private String porteDoCliente;
    private String naturezaDoCliente;

    private String instituicaoFinanceiraCredenciada;
    private String cnpjDoAgenteFinanceiro;
    private String situacaoDaOperacao;
}
