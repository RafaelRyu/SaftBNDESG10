package com.example.demo.config;

import com.example.demo.repository.OperacaoBNDESRepository;
import com.example.demo.service.OperacaoBNDESService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final OperacaoBNDESRepository repository;
    private final OperacaoBNDESService service;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("Banco vazio. Iniciando carga automática do CSV...");
            long total = service.carregarDados();
            log.info("Carga automática finalizada. {} operações importadas.", total);
        } else {
            log.info("Banco já contém dados. Nenhuma carga automática necessária.");
        }
    }
}