package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgenteConcentracaoDTO {
    private String instituicaoFinanceira;
    private long quantidadeOperacoes;
    private double totalFinanciado;
}