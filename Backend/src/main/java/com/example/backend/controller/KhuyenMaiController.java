package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.KhuyenMaiRequest;
import com.example.backend.entity.KhuyenMai;
import com.example.backend.service.KhuyenMaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/khuyen-mai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KhuyenMaiController {

    private final KhuyenMaiService khuyenMaiService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<KhuyenMai>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(khuyenMaiService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhuyenMai>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(khuyenMaiService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KhuyenMai>> create(@Valid @RequestBody KhuyenMaiRequest request) {
        KhuyenMai created = khuyenMaiService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo khuyến mãi thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhuyenMai>> update(
            @PathVariable Integer id,
            @Valid @RequestBody KhuyenMaiRequest request) {
        KhuyenMai updated = khuyenMaiService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật khuyến mãi thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        khuyenMaiService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa khuyến mãi thành công", null));
    }

    @PostMapping("/ap-ma")
    public ResponseEntity<ApiResponse<Map<String, Object>>> applyCode(
            @RequestParam String maCode,
            @RequestParam BigDecimal tongDon) {
        Map<String, Object> result = khuyenMaiService.applyCode(maCode, tongDon);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
