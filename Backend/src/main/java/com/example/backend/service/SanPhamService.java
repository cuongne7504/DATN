package com.example.backend.service;

import com.example.backend.dto.SanPhamRequest;
import com.example.backend.entity.SanPham;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;

    public List<SanPham> getAll() {
        return sanPhamRepository.findAll();
    }

    public SanPham getById(Integer id) {
        return sanPhamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + id));
    }

    @Transactional
    public SanPham create(SanPhamRequest request) {
        SanPham sanPham = new SanPham();
        sanPham.setMaSanPham(generateNextId());
        mapRequestToEntity(request, sanPham);
        sanPham.setNgayTao(LocalDateTime.now());
        return sanPhamRepository.save(sanPham);
    }

    @Transactional
    public SanPham update(Integer id, SanPhamRequest request) {
        SanPham sanPham = getById(id);
        mapRequestToEntity(request, sanPham);
        return sanPhamRepository.save(sanPham);
    }

    @Transactional
    public void delete(Integer id) {
        SanPham sanPham = getById(id);
        sanPhamRepository.delete(sanPham);
    }

    private void mapRequestToEntity(SanPhamRequest request, SanPham sanPham) {
        sanPham.setMaDanhMuc(request.getMaDanhMuc());
        sanPham.setMaThuongHieu(request.getMaThuongHieu());
        sanPham.setTenSanPham(request.getTenSanPham());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setGiaGoc(request.getGiaGoc());
        sanPham.setGiaKhuyenMai(request.getGiaKhuyenMai());
    }

    private Integer generateNextId() {
        return sanPhamRepository.findAll().stream()
                .mapToInt(SanPham::getMaSanPham)
                .max()
                .orElse(0) + 1;
    }
}
