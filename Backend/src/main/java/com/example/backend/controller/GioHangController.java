package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.GioHangDetailResponse;
import com.example.backend.dto.GioHangItemRequest;
import com.example.backend.service.GioHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gio-hang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GioHangController {

    private final GioHangService gioHangService;

    @GetMapping("/{maNguoiDung}")
    public ResponseEntity<ApiResponse<GioHangDetailResponse>> getCartDetail(@PathVariable Integer maNguoiDung) {
        return ResponseEntity.ok(ApiResponse.ok(gioHangService.getCartDetail(maNguoiDung)));
    }

    @PostMapping("/{maNguoiDung}/items")
    public ResponseEntity<ApiResponse<GioHangDetailResponse>> addItem(
            @PathVariable Integer maNguoiDung,
            @Valid @RequestBody GioHangItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(gioHangService.addItem(maNguoiDung, request)));
    }

    @PutMapping("/{maNguoiDung}/items/{maCtGioHang}")
    public ResponseEntity<ApiResponse<GioHangDetailResponse>> updateItem(
            @PathVariable Integer maNguoiDung,
            @PathVariable Integer maCtGioHang,
            @RequestParam Integer soLuong) {
        return ResponseEntity.ok(ApiResponse.ok(gioHangService.updateItem(maNguoiDung, maCtGioHang, soLuong)));
    }

    @DeleteMapping("/{maNguoiDung}/items/{maCtGioHang}")
    public ResponseEntity<ApiResponse<GioHangDetailResponse>> removeItem(
            @PathVariable Integer maNguoiDung,
            @PathVariable Integer maCtGioHang) {
        return ResponseEntity.ok(ApiResponse.ok(gioHangService.removeItem(maNguoiDung, maCtGioHang)));
    }

    @DeleteMapping("/{maNguoiDung}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Integer maNguoiDung) {
        gioHangService.clearCart(maNguoiDung);
        return ResponseEntity.ok(ApiResponse.ok("Xóa giỏ hàng thành công", null));
    }
}
