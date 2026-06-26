package com.example.backend.service;

import com.example.backend.dto.HinhAnhSpRequest;
import com.example.backend.entity.HinhAnhSp;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.HinhAnhSpRepository;
import com.example.backend.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HinhAnhSpService {

    private final HinhAnhSpRepository hinhAnhSpRepository;
    private final SanPhamRepository sanPhamRepository;

    public List<HinhAnhSp> getAll() {
        return hinhAnhSpRepository.findAll();
    }

    public List<HinhAnhSp> getByMaSanPham(Integer maSanPham) {
        return hinhAnhSpRepository.findByMaSanPham(maSanPham);
    }

    public HinhAnhSp getById(Integer id) {
        return hinhAnhSpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hình ảnh sản phẩm có mã: " + id));
    }

    @Transactional
    public HinhAnhSp create(HinhAnhSpRequest request) {
        // Kiểm tra sản phẩm tồn tại
        if (!sanPhamRepository.existsById(request.getMaSanPham())) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + request.getMaSanPham());
        }

        Boolean isMain = request.getLaAnhChinh() != null && request.getLaAnhChinh();

        // Nếu ảnh mới là ảnh chính, bỏ chọn ảnh chính cũ của sản phẩm này
        if (isMain) {
            resetCurrentMainImage(request.getMaSanPham());
        }

        HinhAnhSp hinhAnh = new HinhAnhSp();
        hinhAnh.setMaHinhAnh(generateNextId());
        hinhAnh.setMaSanPham(request.getMaSanPham());
        hinhAnh.setDuongDanAnh(request.getDuongDanAnh());
        hinhAnh.setLaAnhChinh(isMain);

        return hinhAnhSpRepository.save(hinhAnh);
    }

    @Transactional
    public HinhAnhSp update(Integer id, HinhAnhSpRequest request) {
        HinhAnhSp hinhAnh = getById(id);

        if (!sanPhamRepository.existsById(request.getMaSanPham())) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + request.getMaSanPham());
        }

        Boolean isMain = request.getLaAnhChinh() != null && request.getLaAnhChinh();

        // Nếu cập nhật thành ảnh chính, reset các ảnh chính khác
        if (isMain && !hinhAnh.getLaAnhChinh()) {
            resetCurrentMainImage(request.getMaSanPham());
        }

        hinhAnh.setMaSanPham(request.getMaSanPham());
        hinhAnh.setDuongDanAnh(request.getDuongDanAnh());
        hinhAnh.setLaAnhChinh(isMain);

        return hinhAnhSpRepository.save(hinhAnh);
    }

    @Transactional
    public void delete(Integer id) {
        HinhAnhSp hinhAnh = getById(id);
        hinhAnhSpRepository.delete(hinhAnh);
    }

    private void resetCurrentMainImage(Integer maSanPham) {
        Optional<HinhAnhSp> currentMain = hinhAnhSpRepository.findByMaSanPhamAndLaAnhChinh(maSanPham, true);
        if (currentMain.isPresent()) {
            HinhAnhSp mainImg = currentMain.get();
            mainImg.setLaAnhChinh(false);
            hinhAnhSpRepository.save(mainImg);
        }
    }

    private Integer generateNextId() {
        return hinhAnhSpRepository.findAll().stream()
                .mapToInt(HinhAnhSp::getMaHinhAnh)
                .max()
                .orElse(0) + 1;
    }
}
