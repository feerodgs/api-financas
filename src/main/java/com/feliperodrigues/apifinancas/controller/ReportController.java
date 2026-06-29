package com.feliperodrigues.apifinancas.controller;

import com.feliperodrigues.apifinancas.config.OpenApiConfig;
import com.feliperodrigues.apifinancas.domain.User;
import com.feliperodrigues.apifinancas.dto.report.MonthlySummaryResponse;
import com.feliperodrigues.apifinancas.dto.report.PeriodSummaryResponse;
import com.feliperodrigues.apifinancas.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Resumos financeiros por período e categoria")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    @Operation(summary = "Resumo mensal", description = "Retorna totais de receita, despesa, saldo e breakdown por categoria para um mês específico")
    public ResponseEntity<MonthlySummaryResponse> monthly(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Mês (1-12)", required = true) @RequestParam int month,
            @Parameter(description = "Ano (ex: 2026)", required = true) @RequestParam int year) {
        return ResponseEntity.ok(reportService.getMonthlySummary(user, month, year));
    }

    @GetMapping("/by-period")
    @Operation(summary = "Resumo por período", description = "Retorna totais e breakdown por categoria para um intervalo de datas customizado")
    public ResponseEntity<PeriodSummaryResponse> byPeriod(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Data inicial (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Data final (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getPeriodSummary(user, startDate, endDate));
    }
}
