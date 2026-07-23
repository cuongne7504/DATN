package com.example.backend.service;

import com.example.backend.dto.KhuyenMaiRequest;
import com.example.backend.entity.KhuyenMai;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.KhuyenMaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KhuyenMaiService {

    private final KhuyenMaiRepository khuyenMaiRepository;

    public List<KhuyenMai> getAll() {
        return khuyenMaiRepository.findAll();
    }

    public KhuyenMai getById(Integer id) {
        return khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi có mã: " + id));
    }

    @Transactional
    public KhuyenMai create(KhuyenMaiRequest request) {
        // Kiểm tra mã code trùng
        if (khuyenMaiRepository.findByMaCode(request.getMaCode()).isPresent()) {
            throw new BadRequestException("Mã code đã tồn tại: " + request.getMaCode());
        }

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        KhuyenMai km = new KhuyenMai();
        mapRequestToEntity(request, km);
        return khuyenMaiRepository.save(km);
    }

    @Transactional
    public KhuyenMai update(Integer id, KhuyenMaiRequest request) {
        KhuyenMai km = getById(id);

        // Kiểm tra code trùng với bản ghi khác
        khuyenMaiRepository.findByMaCode(request.getMaCode()).ifPresent(existing -> {
            if (!existing.getMaKhuyenMai().equals(id)) {
                throw new BadRequestException("Mã code đã được sử dụng: " + request.getMaCode());
            }
        });

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        mapRequestToEntity(request, km);
        return khuyenMaiRepository.save(km);
    }

    @Transactional
    public void delete(Integer id) {
        KhuyenMai km = getById(id);
        khuyenMaiRepository.delete(km);
    }

    /**
     * Áp mã khuyến mãi và trả về số tiền được giảm.
     * @param maCode  mã code cần áp
     * @param tongDon tổng đơn hàng trước giảm
     * @return Map gồm: soTienGiam, maKhuyenMai
     */
    public Map<String, Object> applyCode(String maCode, BigDecimal tongDon) {
        KhuyenMai km = khuyenMaiRepository.findByMaCode(maCode)
                .orElseThrow(() -> new BadRequestException("Mã khuyến mãi không tồn tại: " + maCode));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(km.getNgayBatDau()) || now.isAfter(km.getNgayKetThuc())) {
            throw new BadRequestException("Mã khuyến mãi đã hết hạn hoặc chưa có hiệu lực");
        }

        if (km.getSoLuongDung() != null && km.getSoLuongDung() <= 0) {
            throw new BadRequestException("Mã khuyến mãi đã hết lượt sử dụng");
        }

        if (km.getDonToiThieu() != null && tongDon.compareTo(km.getDonToiThieu()) < 0) {
            throw new BadRequestException("Đơn hàng tối thiểu " + km.getDonToiThieu() + " đ để áp mã này");
        }

        BigDecimal soTienGiam = BigDecimal.ZERO;
        if (km.getPhanTramGiam() != null && km.getPhanTramGiam() > 0) {
            soTienGiam = tongDon.multiply(BigDecimal.valueOf(km.getPhanTramGiam())).divide(BigDecimal.valueOf(100));
        } else if (km.getSoTienGiam() != null) {
            soTienGiam = km.getSoTienGiam();
        }

        // Đảm bảo không giảm quá tổng đơn
        if (soTienGiam.compareTo(tongDon) > 0) {
            soTienGiam = tongDon;
        }

        // Trừ số lượng sử dụng mã
        if (km.getSoLuongDung() != null) {
            km.setSoLuongDung(km.getSoLuongDung() - 1);
            khuyenMaiRepository.save(km);
        }

        return Map.of(
                "maKhuyenMai", km.getMaKhuyenMai(),
                "soTienGiam", soTienGiam,
                "tongSauGiam", tongDon.subtract(soTienGiam),
                "soLuongConLai", km.getSoLuongDung(),
                "thongBao", "Áp mã thành công! Giảm " + soTienGiam + " đ"
        );
    }

    private void mapRequestToEntity(KhuyenMaiRequest request, KhuyenMai km) {
        km.setMaCode(request.getMaCode());
        km.setPhanTramGiam(request.getPhanTramGiam());
        km.setSoTienGiam(request.getSoTienGiam());
        km.setDonToiThieu(request.getDonToiThieu());
        km.setNgayBatDau(request.getNgayBatDau());
        km.setNgayKetThuc(request.getNgayKetThuc());
        km.setSoLuongDung(request.getSoLuongDung());
    }

    private Integer generateNextId() {
        return khuyenMaiRepository.findAll().stream()
                .mapToInt(KhuyenMai::getMaKhuyenMai).max().orElse(0) + 1;
    }
}

