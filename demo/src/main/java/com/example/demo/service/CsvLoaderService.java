package com.example.demo.service;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.example.demo.model.OperacaoBNDES;
import com.example.demo.repository.OperacaoBNDESRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvLoaderService {

    private final OperacaoBNDESRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.csv.file-path:classpath:operacoes.csv}")
    private Resource csvResource;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Transactional
    public long carregarCsv() {
        long totalImportados = 0;
        final int BATCH_SIZE = 1000;
        List<OperacaoBNDES> lote = new ArrayList<>(BATCH_SIZE);

        try (Reader reader = new InputStreamReader(csvResource.getInputStream(), "UTF-8")) {
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                    .build();

            String[] linha;
            boolean headerEncontrado = false;
            int linhaNum = 0;

            while ((linha = csvReader.readNext()) != null) {
                linhaNum++;

                if (linhaNum <= 5) {
                    log.info("Linha {} tem {} colunas: {}", linhaNum, linha.length, Arrays.toString(linha));
                }

                if (!headerEncontrado) {
                    if (linha.length > 0 && linha[0].trim().replace("\uFEFF", "").startsWith("_id")) {
                        headerEncontrado = true;
                        log.info("Cabeçalho reconhecido na linha {}", linhaNum);
                        continue;
                    }
                    continue;
                }

                if (linha.length < 10) {
                    log.warn("Linha {} ignorada: apenas {} colunas", linhaNum, linha.length);
                    continue;
                }

                try {
                    OperacaoBNDES op = OperacaoBNDES.builder()
                            .cliente(getString(linha, 1))
                            .cpfCnpj(getString(linha, 2))
                            .uf(getString(linha, 3))
                            .municipio(getString(linha, 4))
                            .municipioCodigo(getString(linha, 5))
                            .dataDaContratacao(parseData(getString(linha, 6)))
                            .valorDaOperacaoEmReais(parseDouble(getString(linha, 7)))
                            .valorDesembolsadoReais(parseDouble(getString(linha, 8)))
                            .fonteDeRecursoDesembolsos(getString(linha, 9))
                            .custoFinanceiro(getString(linha, 10))
                            .juros(parseDouble(getString(linha, 11)))
                            .prazoCarenciaMeses(parseInteger(getString(linha, 12)))
                            .prazoAmortizacaoMeses(parseInteger(getString(linha, 13)))
                            .modalidadeDeApoio(getString(linha, 14))
                            .formaDeApoio(getString(linha, 15))
                            .produto(getString(linha, 16))
                            .instrumentoFinanceiro(getString(linha, 17))
                            .inovacao(getString(linha, 18))
                            .areaOperacional(getString(linha, 19))
                            .setorCnae(getString(linha, 20))
                            .subsetorCnaeAgrupado(getString(linha, 21))
                            .subsetorCnaeCodigo(getString(linha, 22))
                            .subsetorCnaeNome(getString(linha, 23))
                            .setorBndes(getString(linha, 24))
                            .subsetorBndes(getString(linha, 25))
                            .porteDoCliente(getString(linha, 26))
                            .naturezaDoCliente(getString(linha, 27))
                            .instituicaoFinanceiraCredenciada(getString(linha, 28))
                            .cnpjDoAgenteFinanceiro(getString(linha, 29))
                            .situacaoDaOperacao(getString(linha, 30))
                            .build();

                    lote.add(op);

                    if (lote.size() >= BATCH_SIZE) {
                        repository.saveAll(lote);
                        entityManager.flush();
                        entityManager.clear();
                        totalImportados += lote.size();
                        log.info("Importados {} registros até agora...", totalImportados);
                        lote.clear();
                    }
                } catch (Exception e) {
                    log.warn("Erro ao processar linha {}: {}", linhaNum, e.getMessage());
                }
            }

            // Processa o último lote
            if (!lote.isEmpty()) {
                repository.saveAll(lote);
                entityManager.flush();
                entityManager.clear();
                totalImportados += lote.size();
            }

        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler arquivo CSV", e);
        }

        log.info("Carga concluída: {} registros importados.", totalImportados);
        return totalImportados;
    }

    // Métodos auxiliares (getString, parseDouble, parseInteger, parseData) permanecem iguais
    private String getString(String[] linha, int idx) {
        return (idx < linha.length) ? linha[idx].trim() : null;
    }

    private Double parseDouble(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDateTime parseData(String data) {
        if (data == null || data.isBlank()) return null;
        try {
            return LocalDateTime.parse(data.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}