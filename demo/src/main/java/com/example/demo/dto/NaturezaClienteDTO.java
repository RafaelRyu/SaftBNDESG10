package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NaturezaClienteDTO {
    private String natureza;
    private long quantidade;
    private double totalFinanciado;
}