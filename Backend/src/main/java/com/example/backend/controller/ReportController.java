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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bao-cao")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/loi-nhuan")
    public ResponseEntity<ApiResponse<List<ProfitReportItemDto>>> getProfitReport() {
        return ResponseEntity.ok(ApiResponse.ok("OK", reportService.calculateProfitReport()));
    }

    @GetMapping("/loi-nhuan/export")
    public ResponseEntity<byte[]> exportProfitReportToExcel(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws IOException {
        List<ProfitReportItemDto> report = reportService.calculateProfitReport();

        // Lọc theo khoảng thời gian nếu có
        if (startDate != null && !startDate.trim().isEmpty()) {
            java.time.LocalDateTime start = java.time.LocalDate.parse(startDate).atStartOfDay();
            report = report.stream()
                    .filter(item -> item.getNgayDat() != null && (item.getNgayDat().isAfter(start) || item.getNgayDat().isEqual(start)))
                    .collect(java.util.stream.Collectors.toList());
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            java.time.LocalDateTime end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
            report = report.stream()
                    .filter(item -> item.getNgayDat() != null && (item.getNgayDat().isBefore(end) || item.getNgayDat().isEqual(end)))
                    .collect(java.util.stream.Collectors.toList());
        }

        byte[] excelBytes = reportService.exportProfitReportToExcel(report);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "bao_cao_loi_nhuan.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}
