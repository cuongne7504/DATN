package com.example.backend.service.impl;

import com.example.backend.dto.YeuCauHoanHangRequest;
import com.example.backend.entity.ChiTietDonHang;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.entity.DonHang;
import com.example.backend.entity.YeuCauHoanHang;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ChiTietDonHangRepository;
import com.example.backend.repository.ChiTietSanPhamRepository;
import com.example.backend.repository.DonHangRepository;
import com.example.backend.repository.YeuCauHoanHangRepository;
import com.example.backend.service.YeuCauHoanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class YeuCauHoanHangServiceImpl implements YeuCauHoanHangService {

    @Autowired
    private YeuCauHoanHangRepository yeuCauRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Override
    @Transactional
    public YeuCauHoanHang taoYeuCau(YeuCauHoanHangRequest request) {
        DonHang donHang = donHangRepository.findById(request.getMaDonHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if (!"Đã giao hàng".equals(donHang.getTrangThai())) {
            throw new BadRequestException("Chỉ có thể hoàn hàng cho đơn hàng đã giao thành công");
        }
        
        long daysBetween = ChronoUnit.DAYS.between(donHang.getNgayDat(), LocalDateTime.now());
        if (daysBetween > 30) {
            throw new BadRequestException("Đã quá hạn đổi/trả hàng");
        }

        YeuCauHoanHang yeuCau = new YeuCauHoanHang();
        yeuCau.setMaDonHang(request.getMaDonHang());
        yeuCau.setLyDo(request.getLyDo());
        yeuCau.setHinhAnhMinhHoa(request.getHinhAnhMinhHoa());
        yeuCau.setSoTienHoan(request.getSoTienHoan());
        yeuCau.setTrangThai("Chờ duyệt");
        yeuCau.setNgayTao(LocalDateTime.now());
        yeuCau.setNgayCapNhat(LocalDateTime.now());

        donHang.setTrangThai("Yêu cầu hoàn hàng");
        donHangRepository.save(donHang);

        return yeuCauRepository.save(yeuCau);
    }

    @Override
    public List<YeuCauHoanHang> getTatCaYeuCau() {
        return yeuCauRepository.findAll();
    }

    @Override
    public List<YeuCauHoanHang> getYeuCauTheoDonHang(Integer maDonHang) {
        return yeuCauRepository.findByMaDonHang(maDonHang);
    }

    @Override
    @Transactional
    public YeuCauHoanHang capNhatTrangThai(Integer maYeuCau, String trangThaiMoi) {
        YeuCauHoanHang yeuCau = yeuCauRepository.findById(maYeuCau)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu hoàn hàng"));

        yeuCau.setTrangThai(trangThaiMoi);
        yeuCau.setNgayCapNhat(LocalDateTime.now());

        DonHang donHang = donHangRepository.findById(yeuCau.getMaDonHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        if ("Đã nhận hàng (Nhập kho)".equals(trangThaiMoi)) {
            List<ChiTietDonHang> chiTietList = chiTietDonHangRepository.findByMaDonHang(donHang.getMaDonHang());
            for (ChiTietDonHang ct : chiTietList) {
                ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ct.getMaChiTietSp()).orElse(null);
                if (ctsp != null) {
                    ctsp.setSoLuongTon(ctsp.getSoLuongTon() + ct.getSoLuong());
                    chiTietSanPhamRepository.save(ctsp);
                }
            }
            donHang.setTrangThai("Đã hoàn hàng");
        } else if ("Từ chối".equals(trangThaiMoi)) {
            donHang.setTrangThai("Đã giao hàng");
        } else if ("Đã hoàn tiền".equals(trangThaiMoi)) {
            donHang.setTrangThai("Đã hoàn tiền");
        } else if ("Đã duyệt".equals(trangThaiMoi)) {
            donHang.setTrangThai("Đang xử lý hoàn hàng");
        }

        donHangRepository.save(donHang);
        return yeuCauRepository.save(yeuCau);
    }
}
