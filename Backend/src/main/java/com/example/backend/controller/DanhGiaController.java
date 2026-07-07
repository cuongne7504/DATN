package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DanhGiaRequest;
import com.example.backend.entity.DanhGia;
import com.example.backend.service.DanhGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/danh-gia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DanhGiaController {

    private final DanhGiaService danhGiaService;

    @GetMapping("/san-pham/{maSanPham}")
    public ResponseEntity<ApiResponse<List<DanhGia>>> getByMaSanPham(@PathVariable Integer maSanPham) {
        return ResponseEntity.ok(ApiResponse.ok(danhGiaService.getByMaSanPham(maSanPham)));
    }

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    public ResponseEntity<ApiResponse<List<DanhGia>>> getByMaNguoiDung(@PathVariable Integer maNguoiDung) {
        return ResponseEntity.ok(ApiResponse.ok(danhGiaService.getByMaNguoiDung(maNguoiDung)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DanhGia>> create(@Valid @RequestBody DanhGiaRequest request) {
        DanhGia created = danhGiaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm đánh giá thành công", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        danhGiaService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa đánh giá thành công", null));
    }
}
