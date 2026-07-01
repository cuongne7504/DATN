package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.KhachHangRequest;
import com.example.backend.dto.KhachHangResponse;
import com.example.backend.service.KhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KhachHangController {

    private final KhachHangService khachHangService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<KhachHangResponse>>> getAll(@RequestParam(value = "search", required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(khachHangService.search(search)));
        }
        return ResponseEntity.ok(ApiResponse.ok(khachHangService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhachHangResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(khachHangService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KhachHangResponse>> create(@RequestBody KhachHangRequest request) {
        KhachHangResponse created = khachHangService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm khách hàng thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhachHangResponse>> update(@PathVariable Integer id, @RequestBody KhachHangRequest request) {
        KhachHangResponse updated = khachHangService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật khách hàng thành công", updated));
    }
    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<ApiResponse<KhachHangResponse>> changeStatus(@PathVariable Integer id, @RequestParam Boolean trangThai) {
        KhachHangResponse updated = khachHangService.changeStatus(id, trangThai);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái khách hàng thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        khachHangService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Khách hàng đã ngừng hoạt động", null));
    }
}
