package com.example.backend.service;

import com.example.backend.entity.SanPham;
import com.example.backend.entity.SanPhamYeuThich;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SanPhamRepository;
import com.example.backend.repository.SanPhamYeuThichRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SanPhamYeuThichService {

    private final SanPhamYeuThichRepository yeuThichRepository;
    private final SanPhamRepository sanPhamRepository;

    public List<SanPhamYeuThich> getByMaNguoiDung(Integer maNguoiDung) {
        return yeuThichRepository.findByMaNguoiDung(maNguoiDung);
    }

    @Transactional
    public SanPhamYeuThich toggle(Integer maNguoiDung, Integer maSanPham) {
        var existing = yeuThichRepository.findByMaNguoiDungAndSanPham_MaSanPham(maNguoiDung, maSanPham);
        if (existing.isPresent()) {
            yeuThichRepository.delete(existing.get());
            return null;
        } else {
            SanPham sp = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham"));
            SanPhamYeuThich newYt = new SanPhamYeuThich();
            newYt.setMaNguoiDung(maNguoiDung);
            newYt.setSanPham(sp);
            newYt.setNgayTao(LocalDateTime.now());
            return yeuThichRepository.save(newYt);
        }
    }

    @Transactional
    public void delete(Integer id) {
        yeuThichRepository.deleteById(id);
    }
}
