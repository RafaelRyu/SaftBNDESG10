// Custo do Dinheiro (categorização por taxa)
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustoDinheiroDTO {
    private String custoFinanceiro;
    private long quantidade;
    private double totalFinanciado;
}