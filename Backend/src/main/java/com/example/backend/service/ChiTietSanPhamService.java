package com.example.backend.service;

import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.repository.ChiTietSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiTietSanPhamService {

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private SkuGeneratorService skuGeneratorService;

    // Lấy tất cả biến thể của một sản phẩm
    public List<ChiTietSanPham> getByMaSanPham(Integer maSanPham) {
        return chiTietSanPhamRepository.findByMaSanPham(maSanPham);
    }

    public ChiTietSanPham getById(Integer id) {
        return chiTietSanPhamRepository.findById(id)
            .orElseThrow(() -> new com.example.backend.exception.ResourceNotFoundException("Không tìm thấy biến thể"));
    }

    public ChiTietSanPham getByMaVachSku(String sku) {
        return chiTietSanPhamRepository.findByMaVachSku(sku)
            .orElseThrow(() -> new com.example.backend.exception.ResourceNotFoundException("Không tìm thấy biến thể với SKU: " + sku));
    }

    // Thêm biến thể mới (Tự động sinh mã vạch)
    public ChiTietSanPham themMoi(ChiTietSanPham chiTiet) {
        // Tự động cấp phát mã vạch SKU độc nhất cho biến thể này
        String uniqueSku = skuGeneratorService.generateUniqueSku();
        chiTiet.setMaVachSku(uniqueSku);
        
        return chiTietSanPhamRepository.save(chiTiet);
    }

    // Cập nhật số lượng hoặc giá cộng thêm
    public ChiTietSanPham capNhat(Integer id, ChiTietSanPham data) {
        ChiTietSanPham existing = chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể SP này!"));
        
        existing.setMauSac(data.getMauSac());
        existing.setKichCo(data.getKichCo());
        existing.setSoLuongTon(data.getSoLuongTon());
        existing.setGiaCongThem(data.getGiaCongThem());
        // KHÔNG cho phép sửa mã vạch SKU vì mã vạch là cố định
        
        return chiTietSanPhamRepository.save(existing);
    }

    public void xoa(Integer id) {
        chiTietSanPhamRepository.deleteById(id);
    }
}
