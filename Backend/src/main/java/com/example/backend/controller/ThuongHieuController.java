package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.ThuongHieu;
import com.example.backend.service.ThuongHieuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/thuong-hieu", "/api/thuonghieu"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ThuongHieuController {

    private final ThuongHieuService thuongHieuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThuongHieu>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lấy thương hiệu thành công", thuongHieuService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ThuongHieu>> getById(@PathVariable Integer id) {
        return thuongHieuService.getById(id)
                .map(t -> ResponseEntity.ok(ApiResponse.ok("Lấy thương hiệu thành công", t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ThuongHieu>> create(@RequestBody ThuongHieu thuongHieu) {
        return ResponseEntity.ok(ApiResponse.ok("Tạo thương hiệu thành công", thuongHieuService.save(thuongHieu)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ThuongHieu>> update(@PathVariable Integer id, @RequestBody ThuongHieu thuongHieu) {
        if (!thuongHieuService.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        thuongHieu.setMaThuongHieu(id);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thương hiệu thành công", thuongHieuService.save(thuongHieu)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        if (!thuongHieuService.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        thuongHieuService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa thương hiệu thành công", null));
    }
}
