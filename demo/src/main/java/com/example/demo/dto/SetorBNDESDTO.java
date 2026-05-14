package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetorBNDESDTO {
    private String setor;
    private long quantidade;
    private double totalFinanciado;
}