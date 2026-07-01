package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.HinhAnhSpRequest;
import com.example.backend.entity.HinhAnhSp;
import com.example.backend.service.HinhAnhSpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hinh-anh")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HinhAnhSpController {

    private final HinhAnhSpService hinhAnhSpService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HinhAnhSp>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(hinhAnhSpService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HinhAnhSp>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(hinhAnhSpService.getById(id)));
    }

    @GetMapping("/san-pham/{maSanPham}")
    public ResponseEntity<ApiResponse<List<HinhAnhSp>>> getByMaSanPham(@PathVariable Integer maSanPham) {
        return ResponseEntity.ok(ApiResponse.ok(hinhAnhSpService.getByMaSanPham(maSanPham)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HinhAnhSp>> create(@Valid @RequestBody HinhAnhSpRequest request) {
        HinhAnhSp created = hinhAnhSpService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm hình ảnh sản phẩm thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HinhAnhSp>> update(
            @PathVariable Integer id,
            @Valid @RequestBody HinhAnhSpRequest request) {
        HinhAnhSp updated = hinhAnhSpService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật hình ảnh sản phẩm thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        hinhAnhSpService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa hình ảnh sản phẩm thành công", null));
    }
}
