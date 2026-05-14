package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumoEstadoDTO {
    private String uf;
    private Double totalFinanciado;
}
