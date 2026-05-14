// Vocação Regional (cruzamento UF x Subsetor CNAE)
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VocacaoRegionalDTO {
    private String uf;
    private String subsetorCnaeNome;
    private long quantidadeOperacoes;
    private double totalFinanciado;
}