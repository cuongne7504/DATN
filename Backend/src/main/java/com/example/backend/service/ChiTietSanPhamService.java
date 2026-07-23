package com.example.backend.service;

import com.example.backend.dto.ChiTietSanPhamRequest;
import com.example.backend.dto.TonKhoResponse;
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
    private final SkuGeneratorService skuGeneratorService;

    public List<ChiTietSanPham> getAll() {
        return chiTietSanPhamRepository.findAll();
    }

    public List<ChiTietSanPham> getByMaSanPham(Integer maSanPham) {
        return chiTietSanPhamRepository.findByMaSanPham(maSanPham);
    }

    public List<TonKhoResponse> layDanhSachTonKho() {
        return chiTietSanPhamRepository.layDanhSachTonKho();
    }

    public ChiTietSanPham getById(Integer id) {
        return chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể có mã: " + id));
    }

    public ChiTietSanPham getByMaVachSku(String sku) {
        return chiTietSanPhamRepository.findByMaVachSku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể với SKU: " + sku));
    }
    @Transactional
    public ChiTietSanPham create(ChiTietSanPhamRequest request) {
        if (!sanPhamRepository.existsById(request.getMaSanPham())) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + request.getMaSanPham());
        }

        Optional<ChiTietSanPham> duplicate = chiTietSanPhamRepository.findByMaVachSku(request.getMaVachSku());
        if (duplicate.isPresent()) {
            throw new IllegalArgumentException("Mã vạch SKU đã tồn tại trong hệ thống: " + request.getMaVachSku());
        }

        ChiTietSanPham chiTiet = new ChiTietSanPham();
        mapRequestToEntity(request, chiTiet);
        return chiTietSanPhamRepository.save(chiTiet);
    }

    @Transactional
    public ChiTietSanPham update(Integer id, ChiTietSanPhamRequest request) {
        ChiTietSanPham chiTiet = getById(id);

        if (!sanPhamRepository.existsById(request.getMaSanPham())) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm có mã: " + request.getMaSanPham());
        }

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

    @Transactional
    public ChiTietSanPham themMoi(ChiTietSanPham chiTiet) {
        if (chiTiet.getMaVachSku() == null || chiTiet.getMaVachSku().trim().isEmpty()) {
            chiTiet.setMaVachSku(skuGeneratorService.generateUniqueSku());
        }
        return chiTietSanPhamRepository.save(chiTiet);
    }

    @Transactional
    public ChiTietSanPham capNhat(Integer id, ChiTietSanPham data) {
        ChiTietSanPham existing = getById(id);

        existing.setMauSac(data.getMauSac());
        existing.setKichCo(data.getKichCo());
        existing.setSoLuongTon(data.getSoLuongTon());
        existing.setGiaCongThem(data.getGiaCongThem());

        if (data.getMaVachSku() != null && !data.getMaVachSku().trim().isEmpty()) {
            Optional<ChiTietSanPham> duplicate = chiTietSanPhamRepository.findByMaVachSku(data.getMaVachSku());
            if (duplicate.isPresent() && !duplicate.get().getMaChiTietSp().equals(id)) {
                throw new IllegalArgumentException("Mã vạch SKU đã được sử dụng: " + data.getMaVachSku());
            }
            existing.setMaVachSku(data.getMaVachSku());
        }

        return chiTietSanPhamRepository.save(existing);
    }

    @Transactional
    public void xoa(Integer id) {
        chiTietSanPhamRepository.deleteById(id);
    }

    private void mapRequestToEntity(ChiTietSanPhamRequest request, ChiTietSanPham chiTiet) {
        chiTiet.setMaSanPham(request.getMaSanPham());
        chiTiet.setMaVachSku(request.getMaVachSku());
        chiTiet.setMauSac(request.getMauSac());
        chiTiet.setKichCo(request.getKichCo());
        chiTiet.setSoLuongTon(request.getSoLuongTon());
        chiTiet.setGiaCongThem(request.getGiaCongThem());
    }
}
