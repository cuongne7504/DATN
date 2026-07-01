package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.service.ChiTietSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chi-tiet-san-pham")
@CrossOrigin(origins = "*") // Mở cửa cho Frontend gọi API thoải mái
public class ChiTietSanPhamController {

    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;

    // Xem danh sách các size/màu của 1 sản phẩm
    @GetMapping("/san-pham/{maSanPham}")
    public ApiResponse<List<ChiTietSanPham>> getBySanPham(@PathVariable Integer maSanPham) {
        List<ChiTietSanPham> list = chiTietSanPhamService.getByMaSanPham(maSanPham);
        return ApiResponse.ok("Lấy danh sách biến thể thành công", list);
    }

    // Lấy 1 biến thể bằng ID
    @GetMapping("/{id}")
    public ApiResponse<ChiTietSanPham> getById(@PathVariable Integer id) {
        ChiTietSanPham chiTiet = chiTietSanPhamService.getById(id);
        return ApiResponse.ok("Lấy biến thể thành công", chiTiet);
    }

    // Tìm biến thể bằng mã SKU
    @GetMapping("/sku/{sku}")
    public ApiResponse<ChiTietSanPham> getBySku(@PathVariable String sku) {
        ChiTietSanPham chiTiet = chiTietSanPhamService.getByMaVachSku(sku);
        return ApiResponse.ok("Tìm biến thể bằng SKU thành công", chiTiet);
    }

    // Thêm 1 màu/size mới (Tự sinh mã vạch)
    @PostMapping
    public ApiResponse<ChiTietSanPham> themMoi(@RequestBody ChiTietSanPham chiTiet) {
        ChiTietSanPham saved = chiTietSanPhamService.themMoi(chiTiet);
        return ApiResponse.ok("Thêm biến thể thành công! Đã cấp mã vạch: " + saved.getMaVachSku(), saved);
    }

    // Cập nhật tồn kho/giá
    @PutMapping("/{id}")
    public ApiResponse<ChiTietSanPham> capNhat(@PathVariable Integer id, @RequestBody ChiTietSanPham chiTiet) {
        ChiTietSanPham updated = chiTietSanPhamService.capNhat(id, chiTiet);
        return ApiResponse.ok("Cập nhật biến thể thành công", updated);
    }

    // Xóa biến thể
    @DeleteMapping("/{id}")
    public ApiResponse<String> xoa(@PathVariable Integer id) {
        chiTietSanPhamService.xoa(id);
        return ApiResponse.ok("Xóa biến thể thành công", null);
    }
}
