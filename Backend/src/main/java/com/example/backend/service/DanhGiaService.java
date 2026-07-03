package com.example.backend.service;

import com.example.backend.dto.DanhGiaRequest;
import com.example.backend.entity.DanhGia;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.DanhGiaRepository;
import com.example.backend.repository.NguoiDungRepository;
import com.example.backend.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DanhGiaService {

    private final DanhGiaRepository danhGiaRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public List<DanhGia> getByMaSanPham(Integer maSanPham) {
        if (!sanPhamRepository.existsById(maSanPham)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + maSanPham);
        }
        return danhGiaRepository.findByMaSanPhamOrderByNgayTaoDesc(maSanPham);
    }

    public List<DanhGia> getByMaNguoiDung(Integer maNguoiDung) {
        if (!nguoiDungRepository.existsById(maNguoiDung)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + maNguoiDung);
        }
        return danhGiaRepository.findByMaNguoiDung(maNguoiDung);
    }

    @Transactional
    public DanhGia create(DanhGiaRequest request) {
        if (!nguoiDungRepository.existsById(request.getMaNguoiDung())) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng có mã: " + request.getMaNguoiDung());
        }
        if (!sanPhamRepository.existsById(request.getMaSanPham())) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + request.getMaSanPham());
        }

        DanhGia danhGia = new DanhGia();
        danhGia.setMaNguoiDung(request.getMaNguoiDung());
        danhGia.setMaSanPham(request.getMaSanPham());
        danhGia.setSoSao(request.getSoSao());
        danhGia.setNoiDung(request.getNoiDung());
        danhGia.setNgayTao(LocalDateTime.now());

        return danhGiaRepository.save(danhGia);
    }

    @Transactional
    public void delete(Integer id) {
        DanhGia danhGia = danhGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá có mã: " + id));
        danhGiaRepository.delete(danhGia);
    }

    private Integer generateNextId() {
        return danhGiaRepository.findAll().stream()
                .mapToInt(DanhGia::getMaDanhGia).max().orElse(0) + 1;
    }
}

