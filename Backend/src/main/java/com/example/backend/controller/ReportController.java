package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.ProfitReportItemDto;
import com.example.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/profit")
    public ResponseEntity<ApiResponse<List<ProfitReportItemDto>>> getProfitReport() {
        List<ProfitReportItemDto> report = reportService.calculateProfitReport();
        return ResponseEntity.ok(ApiResponse.ok("Lấy báo cáo lợi nhuận thành công", report));
    }

    @GetMapping("/profit/export/excel")
    public ResponseEntity<byte[]> exportProfitReportToExcel() throws IOException {
        List<ProfitReportItemDto> report = reportService.calculateProfitReport();
        byte[] excelBytes = reportService.exportProfitReportToExcel(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "bao_cao_loi_nhuan.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

}
