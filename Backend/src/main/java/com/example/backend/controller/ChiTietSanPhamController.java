package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.ChiTietSanPhamRequest;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.service.ChiTietSanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biens-the")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChiTietSanPham>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(chiTietSanPhamService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChiTietSanPham>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(chiTietSanPhamService.getById(id)));
    }

    @GetMapping("/san-pham/{maSanPham}")
    public ResponseEntity<ApiResponse<List<ChiTietSanPham>>> getByMaSanPham(@PathVariable Integer maSanPham) {
        return ResponseEntity.ok(ApiResponse.ok(chiTietSanPhamService.getByMaSanPham(maSanPham)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChiTietSanPham>> create(@Valid @RequestBody ChiTietSanPhamRequest request) {
        ChiTietSanPham created = chiTietSanPhamService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm biến thể sản phẩm thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChiTietSanPham>> update(
            @PathVariable Integer id,
            @Valid @RequestBody ChiTietSanPhamRequest request) {
        ChiTietSanPham updated = chiTietSanPhamService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật biến thể sản phẩm thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        chiTietSanPhamService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa biến thể sản phẩm thành công", null));
    }
}
