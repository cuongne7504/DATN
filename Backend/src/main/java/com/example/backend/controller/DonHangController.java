package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.DonHangDetailResponse;
import com.example.backend.dto.GuestCheckoutRequest;
import com.example.backend.dto.TaoDonHangPosRequest;
import com.example.backend.dto.TaoDonHangRequest;
import com.example.backend.entity.DonHang;
import com.example.backend.service.DonHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/don-hang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DonHangController {

    private final DonHangService donHangService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DonHang>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(donHangService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DonHangDetailResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(donHangService.getById(id)));
    }

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    public ResponseEntity<ApiResponse<List<DonHangDetailResponse>>> getByMaNguoiDung(@PathVariable Integer maNguoiDung) {
        return ResponseEntity.ok(ApiResponse.ok(donHangService.getByMaNguoiDung(maNguoiDung)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DonHangDetailResponse>> create(@Valid @RequestBody TaoDonHangRequest request) {
        DonHangDetailResponse created = donHangService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo đơn hàng thành công", created));
    }

    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<DonHangDetailResponse>> createGuestOrder(@Valid @RequestBody GuestCheckoutRequest request) {
        DonHangDetailResponse created = donHangService.createGuestOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo đơn hàng khách vãng lai thành công", created));
    }

    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<ApiResponse<DonHang>> updateTrangThai(
            @PathVariable Integer id,
            @RequestParam String trangThai) {
        DonHang updated = donHangService.updateTrangThai(id, trangThai);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái đơn hàng thành công", updated));
    }

    @PutMapping("/{id}/giao-hang")
    public ResponseEntity<ApiResponse<DonHang>> giaoChoShipper(
            @PathVariable Integer id,
            @RequestBody com.example.backend.dto.GiaoHangRequest request) {
        DonHang updated = donHangService.giaoChoShipper(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Giao hàng cho shipper thành công", updated));
    }

    @PutMapping("/{id}/gui-ghn")
    public ResponseEntity<ApiResponse<DonHang>> guiDonSangGHN(@PathVariable Integer id) {
        DonHang updated = donHangService.guiDonSangGHN(id);
        return ResponseEntity.ok(ApiResponse.ok("Gửi đơn sang GHN thành công", updated));
    }

    @PostMapping("/pos")
    public ResponseEntity<ApiResponse<DonHangDetailResponse>> createPosOrder(@Valid @RequestBody TaoDonHangPosRequest request) {
        DonHangDetailResponse created = donHangService.createPosOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo đơn hàng POS thành công", created));
    }
}
