package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.repository.ChiTietSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/pos")
@CrossOrigin(origins = "*")
public class PosController {

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    /**
     * API siêu tốc dành cho máy quét mã vạch tại quầy thu ngân (POS)
     */
    @GetMapping("/scan/{maVachSku}")
    public ApiResponse<ChiTietSanPham> scanBarcode(@PathVariable String maVachSku) {
        Optional<ChiTietSanPham> chiTiet = chiTietSanPhamRepository.findByMaVachSku(maVachSku);
        
        if (chiTiet.isPresent()) {
            return ApiResponse.ok("Tít mã vạch thành công!", chiTiet.get());
        } else {
            return ApiResponse.fail("Sản phẩm không tồn tại trong hệ thống!");
        }
    }
}
