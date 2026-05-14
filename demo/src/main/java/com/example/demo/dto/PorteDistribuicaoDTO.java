// Distribuição por Porte do Cliente
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PorteDistribuicaoDTO {
    private String porte;
    private long quantidade;
    private double totalFinanciado;
}