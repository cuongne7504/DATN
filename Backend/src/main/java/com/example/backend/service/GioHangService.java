package com.example.backend.service;

import com.example.backend.entity.ChiTietGioHang;
import com.example.backend.entity.GioHang;
import com.example.backend.repository.ChiTietGioHangRepository;
import com.example.backend.repository.GioHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    // Lấy giỏ hàng của user (Nếu chưa có thì tự động tạo mới)
    public GioHang getOrCreateGioHang(Integer maNguoiDung) {
        return gioHangRepository.findByMaNguoiDung(maNguoiDung).orElseGet(() -> {
            GioHang newCart = new GioHang();
            newCart.setMaNguoiDung(maNguoiDung);
            newCart.setNgayTao(LocalDateTime.now());
            return gioHangRepository.save(newCart);
        });
    }

    public List<ChiTietGioHang> getChiTietGioHang(Integer maNguoiDung) {
        GioHang cart = getOrCreateGioHang(maNguoiDung);
        return chiTietGioHangRepository.findByMaGioHang(cart.getMaGioHang());
    }

    public ChiTietGioHang themVaoGio(Integer maNguoiDung, Integer maChiTietSp, Integer soLuong) {
        GioHang cart = getOrCreateGioHang(maNguoiDung);
        
        Optional<ChiTietGioHang> existingItem = chiTietGioHangRepository.findByMaGioHangAndMaChiTietSp(cart.getMaGioHang(), maChiTietSp);
        if (existingItem.isPresent()) {
            ChiTietGioHang item = existingItem.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            return chiTietGioHangRepository.save(item);
        } else {
            ChiTietGioHang newItem = new ChiTietGioHang();
            newItem.setMaGioHang(cart.getMaGioHang());
            newItem.setMaChiTietSp(maChiTietSp);
            newItem.setSoLuong(soLuong);
            return chiTietGioHangRepository.save(newItem);
        }
    }

    public ChiTietGioHang capNhatSoLuong(Integer maCtGioHang, Integer soLuongMoi) {
        ChiTietGioHang item = chiTietGioHangRepository.findById(maCtGioHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));
        item.setSoLuong(soLuongMoi);
        return chiTietGioHangRepository.save(item);
    }

    public void xoaKhoiGio(Integer maCtGioHang) {
        chiTietGioHangRepository.deleteById(maCtGioHang);
    }
}
