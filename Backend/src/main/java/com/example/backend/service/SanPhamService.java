package com.example.backend.service;

import com.example.backend.dto.SanPhamRequest;
import com.example.backend.entity.SanPham;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final HinhAnhSpRepository hinhAnhSpRepository;
    private final DanhGiaRepository danhGiaRepository;
    private final SanPhamYeuThichRepository sanPhamYeuThichRepository;

    public List<SanPham> getAll() {
        return sanPhamRepository.findAll();
    }

    public List<SanPham> search(String ten, Integer maDanhMuc, Integer maThuongHieu, BigDecimal minGia, BigDecimal maxGia, String kichCo, String mauSac) {
        return sanPhamRepository.searchProducts(ten, maDanhMuc, maThuongHieu, minGia, maxGia, kichCo, mauSac);
    }


    public SanPham getById(Integer id) {
        return sanPhamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + id));
    }

    @Transactional
    public SanPham create(SanPhamRequest request) {
        SanPham sanPham = new SanPham();
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

        List<ChiTietSanPham> variants = chiTietSanPhamRepository.findByMaSanPham(id);
        for (ChiTietSanPham ctsp : variants) {
            if (chiTietDonHangRepository.existsByMaChiTietSp(ctsp.getMaChiTietSp())) {
                throw new BadRequestException("Không thể xóa! Sản phẩm này đã phát sinh đơn hàng trong hệ thống.");
            }
        }

        for (ChiTietSanPham ctsp : variants) {
            chiTietGioHangRepository.deleteByMaChiTietSp(ctsp.getMaChiTietSp());
        }

        hinhAnhSpRepository.deleteByMaSanPham(id);
        danhGiaRepository.deleteByMaSanPham(id);
        sanPhamYeuThichRepository.deleteBySanPham_MaSanPham(id);
        chiTietSanPhamRepository.deleteByMaSanPham(id);

        sanPhamRepository.delete(sanPham);
    }

    private void mapRequestToEntity(SanPhamRequest request, SanPham sanPham) {
        sanPham.setMaDanhMuc(request.getMaDanhMuc());
        sanPham.setMaThuongHieu(request.getMaThuongHieu());
        sanPham.setTenSanPham(request.getTenSanPham());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setGiaGoc(request.getGiaGoc());
        sanPham.setGiaKhuyenMai(request.getGiaKhuyenMai() != null ? request.getGiaKhuyenMai() : request.getGiaGoc());
    }

    private Integer generateNextId() {
        return sanPhamRepository.findAll().stream()
                .mapToInt(SanPham::getMaSanPham)
                .max()
                .orElse(0) + 1;
    }
}

