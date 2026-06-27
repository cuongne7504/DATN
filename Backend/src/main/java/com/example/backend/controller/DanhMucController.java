package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.DanhMuc;
import com.example.backend.service.DanhMucService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/danh-muc", "/api/danhmuc"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DanhMucController {

    private final DanhMucService danhMucService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DanhMuc>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lấy danh mục thành công", danhMucService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DanhMuc>> getById(@PathVariable Integer id) {
        return danhMucService.getById(id)
                .map(d -> ResponseEntity.ok(ApiResponse.ok("Lấy danh mục thành công", d)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DanhMuc>> create(@RequestBody DanhMuc danhMuc) {
        return ResponseEntity.ok(ApiResponse.ok("Tạo danh mục thành công", danhMucService.save(danhMuc)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DanhMuc>> update(@PathVariable Integer id, @RequestBody DanhMuc danhMuc) {
        if (!danhMucService.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        danhMuc.setMaDanhMuc(id);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật danh mục thành công", danhMucService.save(danhMuc)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        if (!danhMucService.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        danhMucService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa danh mục thành công", null));
    }
}
