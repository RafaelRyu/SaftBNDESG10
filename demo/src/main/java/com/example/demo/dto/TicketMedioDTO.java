// Ticket Médio (resposta a uma pergunta específica)
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketMedioDTO {
    private long quantidadeOperacoes;
    private double valorTotal;
    private double ticketMedio;
}