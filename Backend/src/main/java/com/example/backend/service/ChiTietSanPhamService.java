package com.example.backend.service;

import com.example.backend.dto.ChiTietSanPhamRequest;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietSanPhamRepository;
import com.example.backend.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChiTietSanPhamService {

    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;

    public List<ChiTietSanPham> getAll() {
        return chiTietSanPhamRepository.findAll();
    }

    public List<ChiTietSanPham> getByMaSanPham(Integer maSanPham) {
        return chiTietSanPhamRepository.findByMaSanPham(maSanPham);
    }

    public ChiTietSanPham getById(Integer id) {
        return chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết sản phẩm có mã: " + id));
    }

    @Transactional
    public ChiTietSanPham create(ChiTietSanPhamRequest request) {
        // Kiểm tra sản phẩm gốc tồn tại
        if (!sanPhamRepository.existsById(request.getMaSanPham())) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + request.getMaSanPham());
        }

        // Kiểm tra trùng mã vạch SKU
        Optional<ChiTietSanPham> duplicate = chiTietSanPhamRepository.findByMaVachSku(request.getMaVachSku());
        if (duplicate.isPresent()) {
            throw new IllegalArgumentException("Mã vạch SKU đã tồn tại trong hệ thống: " + request.getMaVachSku());
        }

        ChiTietSanPham chiTiet = new ChiTietSanPham();
        chiTiet.setMaChiTietSp(generateNextId());
        mapRequestToEntity(request, chiTiet);

        return chiTietSanPhamRepository.save(chiTiet);
    }

    @Transactional
    public ChiTietSanPham update(Integer id, ChiTietSanPhamRequest request) {
        ChiTietSanPham chiTiet = getById(id);

        // Kiểm tra sản phẩm gốc tồn tại
        if (!sanPhamRepository.existsById(request.getMaSanPham())) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + request.getMaSanPham());
        }

        // Kiểm tra trùng mã vạch SKU của bản ghi khác
        Optional<ChiTietSanPham> duplicate = chiTietSanPhamRepository.findByMaVachSku(request.getMaVachSku());
        if (duplicate.isPresent() && !duplicate.get().getMaChiTietSp().equals(id)) {
            throw new IllegalArgumentException("Mã vạch SKU đã được sử dụng bởi biến thể khác: " + request.getMaVachSku());
        }

        mapRequestToEntity(request, chiTiet);
        return chiTietSanPhamRepository.save(chiTiet);
    }

    @Transactional
    public void delete(Integer id) {
        ChiTietSanPham chiTiet = getById(id);
        chiTietSanPhamRepository.delete(chiTiet);
    }

    private void mapRequestToEntity(ChiTietSanPhamRequest request, ChiTietSanPham chiTiet) {
        chiTiet.setMaSanPham(request.getMaSanPham());
        chiTiet.setMaVachSku(request.getMaVachSku());
        chiTiet.setMauSac(request.getMauSac());
        chiTiet.setKichCo(request.getKichCo());
        chiTiet.setSoLuongTon(request.getSoLuongTon());
        chiTiet.setGiaCongThem(request.getGiaCongThem());
    }

    private Integer generateNextId() {
        return chiTietSanPhamRepository.findAll().stream()
                .mapToInt(ChiTietSanPham::getMaChiTietSp)
                .max()
                .orElse(0) + 1;
    }
}
