package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.SanPhamRequest;
import com.example.backend.entity.SanPham;
import com.example.backend.service.SanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/san-pham")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SanPham>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(sanPhamService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SanPham>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(sanPhamService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SanPham>> create(@Valid @RequestBody SanPhamRequest request) {
        SanPham created = sanPhamService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm sản phẩm thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SanPham>> update(
            @PathVariable Integer id,
            @Valid @RequestBody SanPhamRequest request) {
        SanPham updated = sanPhamService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật sản phẩm thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        sanPhamService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa sản phẩm thành công", null));
    }
}
